package chap02iomonad

import chap02iomonad.auth._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/*
  Step 9 adds the 'foreach' method to IO. It executes asynchronously and requires an implicit Executioncontext.

  'foreach' only processes successful results, errors are reported to the ExecutionContext.
 */
object IOApp09 extends App {

  case class IO[A](run: () => A) {

    def map[B](f: A => B): IO[B] = IO { () => f(run()) }
    def flatMap[B](f: A => IO[B]): IO[B] = IO { () => f(run()).run() }

    // ----- impure sync run* methods

    // runs on the current Thread returning Try[A]
    def runToTry: Try[A] = Try { run() }

    // runs on the current Thread returning Either[Throwable, A]
    def runToEither: Either[Throwable, A] = runToTry.toEither

    // ----- impure async run* methods

    // returns a Future that runs the task eagerly on another thread
    def runToFuture(implicit ec: ExecutionContext): Future[A] = Future { run() }

    // runs the IO in a Runnable on the given ExecutionContext
    // and then executes the specified Try based callback
    def runOnComplete(callback: Try[A] => Unit)(implicit ec: ExecutionContext): Unit =
    // convert Try based callback into an Either based callback
      runAsync0(ec, (ea: Either[Throwable, A]) => callback(ea.toTry))

    // runs the IO in a Runnable on the given ExecutionContext
    // and then executes the specified Either based callback
    def runAsync(callback: Either[Throwable, A] => Unit)(implicit ec: ExecutionContext): Unit =
      runAsync0(ec, callback)

    private def runAsync0(ec: ExecutionContext, callback: Either[Throwable, A] => Unit): Unit =
      ec.execute(() => callback(runToEither))

    // Triggers async evaluation of this IO, executing the given function for the generated result.
    // WARNING: Will not be called if this IO is never completed or if it is completed with a failure.
    // Since this method executes asynchronously and does not produce a return value,
    // any non-fatal exceptions thrown will be reported to the ExecutionContext.
    def foreach(f: A => Unit)(implicit ec: ExecutionContext): Unit =
      runAsync {
        case Left(ex) => ec.reportFailure(ex)
        case Right(value) => f(value)
      }
  }

  object IO {
    def pure[A](a: A): IO[A] = IO { () => a }
    def eval[A](a: => A): IO[A] = IO { () => a }
  }



  import Password._
  import User._

  // authenticate impl with for-comprehension
  def authenticate(username: String, password: String): IO[Boolean] =
    for {
      optUser <- IO.eval(getUsers) map { users =>
        users.find(_.name == username)
      }
      isAuthenticated <- IO.eval(getPasswords) map { passwords =>
        optUser.isDefined && passwords.contains(Password(optUser.get.id, password))
      }
    } yield isAuthenticated



  println("\n-----")

  implicit val ec: ExecutionContext = ExecutionContext.global

  IO.eval(getUsers) foreach { users => users foreach println }
  Thread sleep 500L
  println("-----")

  IO.eval(getPasswords) foreach { users => users foreach println }
  Thread sleep 500L
  println("-----")

  println("\n>>> IO#run: authenticate:")
  authenticate("maggie", "maggie-pw") foreach println
  authenticate("maggieXXX", "maggie-pw") foreach println
  authenticate("maggie", "maggie-pwXXX") foreach println


  val checkMaggie: IO[Boolean] = authenticate("maggie", "maggie-pw")

  println("\n>>> IO#runToTry:")
  printAuthTry(checkMaggie.runToTry)

  println("\n>>> IO#runToEither:")
  printAuthEither(checkMaggie.runToEither)

  println("\n>>> IO#runToFuture:")
  val future: Future[Boolean] = checkMaggie.runToFuture
  future onComplete authCallbackTry
  Thread sleep 500L

  println("\n>>> IO#runOnComplete:")
  checkMaggie runOnComplete authCallbackTry
  Thread sleep 500L

  println("\n>>> IO#runAsync:")
  checkMaggie runAsync authCallbackEither
  Thread sleep 500L

  println("-----\n")
}
