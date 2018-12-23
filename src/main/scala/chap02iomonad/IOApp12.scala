package chap02iomonad

import cats.Monad

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import scala.language.higherKinds

/*
  Step 12 makes the abtract 'run' method in trait IO concrete.
  It is implemented as a pattern match over the subtypes of the ADT: Pure and Eval.

  As 'run' is now a concrete method in trait IO the Function0[A] parameter
  can no longer have the same name 'run' in order not ot override the base traits method 'run'.
  I called it 'thunk'.

  The method IO#run can now be made private.
  The other IO#run* methods are provided for public use.
 */
object IOApp12 extends App {

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

    private case class Pure[A](thunk: () => A) extends IO[A]
    private case class Eval[A](thunk: () => A) extends IO[A]

    def pure[A](a: A): IO[A] = Pure { () => a }
    def now[A](a: A): IO[A] = pure(a)

    def eval[A](a: => A): IO[A] = Eval { () => a }
    def delay[A](a: => A): IO[A] = eval(a)
    def apply[A](a: => A): IO[A] = eval(a)

    implicit def ioMonad: Monad[IO] = new Monad[IO] {
      override def pure[A](value: A): IO[A] = IO.pure(value)
      override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa flatMap f
      override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]): IO[B] = ???
    }
  }



  import cats.syntax.functor._
  import cats.syntax.flatMap._

  def sumF[F[_]: Monad](from: Int, to: Int): F[Int] =
    Monad[F].pure { sumOfRange(from, to) }

  def fibonacciF[F[_]: Monad](num: Int): F[BigInt] =
    Monad[F].pure { fibonacci(num) }

  def factorialF[F[_]: Monad](num: Int): F[BigInt] =
    Monad[F].pure { factorial(num) }

  def computeF[F[_]: Monad](from: Int, to: Int): F[BigInt] =
    for {
      x <- sumF(from, to)
      y <- fibonacciF(x)
      z <- factorialF(y.intValue)
    } yield z


  println("\n-----")

  def computeWithIO(): Unit = {

    // reify F[] with IO
    val io: IO[BigInt] = computeF[IO](1, 4)

    implicit val ec: ExecutionContext = ExecutionContext.global
    io foreach { result => println(s"result = $result") }
    //=> 6227020800

    Thread sleep 500L
  }

  def computeWithId(): Unit = {

    import cats.Id

    // reify F[] with Id
    val result: Id[BigInt] = computeF[Id](1, 4)

    println(s"result = $result")
    //=> 6227020800

    Thread sleep 500L
  }

  def computeWithOption(): Unit = {

    import cats.instances.option._

    // reify F[] with Option
    val maybeResult: Option[BigInt] = computeF[Option](1, 4)

    maybeResult foreach { result => println(s"result = $result") }
    //=> 6227020800

    Thread sleep 500L
  }

  computeWithIO()
  computeWithId()
  computeWithOption()

  println("-----\n")
}
