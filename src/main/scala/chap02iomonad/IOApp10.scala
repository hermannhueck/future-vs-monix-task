package chap02iomonad

import chap02iomonad.auth._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/*
  Step 10 makes the abtract 'run' method in trait IO concrete.
  It is implemented as a pattern match over the subtypes of the ADT: Pure and Eval.

  As 'run' is now a concrete method in trait IO the Function0[A] parameter
  can no longer have the same name 'run' in order not ot override the base traits method 'run'.
  I called it 'thunk'.

  The method IO#run can now be made private.
  The other IO#run* methods are provided for public use.
 */
object IOApp10 extends App {

  trait IO[A] {

    import IO._

    private def run(): A = this match {
      case Pure(thunk) => thunk()
      case Eval(thunk) => thunk()
    }

    def map[B](f: A => B): IO[B] = IO { f(run()) }
    def flatMap[B](f: A => IO[B]): IO[B] = IO { f(run()).run() }

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
    def runOnComplete(callback: Try[A] => Unit)(implicit ec: ExecutionContext): Unit = {
      ec.execute(new Runnable {
        override def run(): Unit = callback(runToTry)
      })
    }

    // runs the IO in a Runnable on the given ExecutionContext
    // and then executes the specified Either based callback
    def runAsync(callback: Either[Throwable, A] => Unit)(implicit ec: ExecutionContext): Unit = {
      ec.execute(new Runnable {
        override def run(): Unit = callback(runToEither)
      })
    }

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

    private case class Pure[A](thunk: () => A) extends IO[A]
    private case class Eval[A](thunk: () => A) extends IO[A]

    def pure[A](a: A): IO[A] = Pure { () => a }

    def eval[A](a: => A): IO[A] = Eval { () => a }
    def delay[A](a: => A): IO[A] = eval(a)
    def apply[A](a: => A): IO[A] = eval(a)
  }



  import Password._
  import User._

  // authenticate impl with for-comprehension
  def authenticate(username: String, password: String): IO[Boolean] =
    for {
      optUser <- IO(getUsers) map { users =>
        users.find(_.name == username)
      }
      authenticated <- IO(getPasswords) map { passwords =>
        optUser.isDefined && passwords.contains(Password(optUser.get.id, password))
      }
    } yield authenticated



  println("\n-----")

  implicit val ec: ExecutionContext = ExecutionContext.global

  IO(getUsers) foreach { users => users foreach println }
  Thread sleep 500L
  println("-----")

  IO(getPasswords) foreach { users => users foreach println }
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
  checkMaggie.runToFuture onComplete authCallbackTry
  Thread sleep 500L

  println("\n>>> IO#runOnComplete:")
  checkMaggie runOnComplete authCallbackTry
  Thread sleep 500L

  println("\n>>> IO#runAsync:")
  checkMaggie runAsync authCallbackEither
  Thread sleep 500L

  println("-----\n")
}