package chap02iomonad

import cats.Monad
import chap02iomonad.auth._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/*
  In step 14 I added the subtype FlatMap to the ADT IO and expanded the 'run' method accordingly.
  The Method IO#flatMap just creates an instance of FlatMap.
  IO#map is implemented in terms of IO#flatMap and IO.pure.
  Now IO is trampolined and hence stack-safe.
 */
object IOApp14 extends App {

  trait IO[A] {

    import IO._

    private def run(): A = this match {
      case Pure(thunk) => thunk()
      case Eval(thunk) => thunk()
      case Suspend(thunk) => thunk().run()
      case FlatMap(src, f) => f(src.run()).run()
    }

    def map[B](f: A => B): IO[B] = flatMap(a => pure(f(a)))
    def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

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
      runAsync(ea => callback(ea.toTry)) // convert Try based callback into an Either based callback

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

    private case class Pure[A](thunk: () => A) extends IO[A]
    private case class Eval[A](thunk: () => A) extends IO[A]
    private case class Suspend[A](thunk: () => IO[A]) extends IO[A]
    private case class FlatMap[A, B](src: IO[A], f: A => IO[B]) extends IO[B]

    def pure[A](a: A): IO[A] = Pure { () => a }
    def now[A](a: A): IO[A] = pure(a)

    def eval[A](a: => A): IO[A] = Eval { () => a }
    def delay[A](a: => A): IO[A] = eval(a)
    def apply[A](a: => A): IO[A] = eval(a)

    def suspend[A](ioa: => IO[A]): IO[A] = Suspend(() => ioa)
    def defer[A](ioa: => IO[A]): IO[A] = suspend(ioa)

    implicit def ioMonad: Monad[IO] = new Monad[IO] {
      override def pure[A](value: A): IO[A] = IO.pure(value)
      override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa flatMap f
      override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]): IO[B] = ???
    }
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

  println("\n>>> IO.pure:")
  val io1 = IO.pure { println("immediate side effect"); 5 }
  Thread sleep 2000L
  io1 foreach println
  Thread sleep 2000L

  println("\n>>> IO.defer:")
  val io2 = IO.defer { IO.pure { println("deferred side effect"); 5 } }
  Thread sleep 2000L
  io2 foreach println
  Thread sleep 2000L

  println("-----\n")
}
