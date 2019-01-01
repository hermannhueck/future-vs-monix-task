package chap02iomonad

import cats.Monad

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import scala.language.higherKinds

/*
  Step 9 defines a monad instance for IO.
 */
object IOApp09MonadInstance extends App {

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
  }

  object IO {
    def pure[A](a: A): IO[A] = IO { () => a }
    def eval[A](a: => A): IO[A] = IO { () => a }

    // Monad instance defined in implicit context
    implicit def ioMonad: Monad[IO] = new Monad[IO] {
      override def pure[A](value: A): IO[A] = IO.pure(value)
      override def flatMap[A, B](fa: IO[A])(f: A => IO[B]): IO[B] = fa flatMap f
      override def tailRecM[A, B](a: A)(f: A => IO[Either[A, B]]): IO[B] = ???
    }
  }



  import cats.syntax.flatMap._
  import cats.syntax.functor._

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
    io.runToFuture foreach { result => println(s"result = $result") }
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
