package chap02iomonad

import cats.Monad

import scala.language.higherKinds
import scala.util.Try

/*
  The original IO#run() might throw an exception when run.
  In step 7 I added two additional synchronous run* methods which do not throw an exception:
  'runToTry' and 'runToEither'.
 */
object IOApp07RunSync extends App {

  case class IO[A](run: () => A) {

    import IO._

    def flatMap[B](f: A => IO[B]): IO[B] = IO { () => f(run()).run() }
    def map[B](f: A => B): IO[B] = flatMap(a => pure(f(a)))
    def flatten[B](implicit ev: A <:< IO[B]): IO[B] = flatMap(a => a)

    // ----- impure sync run* methods

    // runs on the current Thread returning Try[A]
    def runToTry: Try[A] = Try { run() }

    // runs on the current Thread returning Either[Throwable, A]
    def runToEither: Either[Throwable, A] = runToTry.toEither
  }

  object IO {
    def pure[A](value: A): IO[A] = IO { () => value }
    def eval[A](thunk: => A): IO[A] = IO { () => thunk }

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



  val io: IO[BigInt] = computeF[IO](1, 4)


  println("\n-----")

  println("\n>>> IO#run:")
  val result: BigInt = io.run()
  println(result)

  println("\n>>> IO#runToTry:")
  val tryy: Try[BigInt] = io.runToTry
  println(tryy)

  println("\n>>> IO#runToEither:")
  val either: Either[Throwable, BigInt] = io.runToEither
  println(either)

  Thread sleep 500L

  println("-----\n")
}
