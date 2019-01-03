package chap02iomonad

import cats.Monad

import scala.language.higherKinds

/*
  Step 6 defines a monad instance for IO.
 */
object IOApp06MonadInstance extends App {

  case class IO[A](run: () => A) {

    import IO._

    def flatMap[B](f: A => IO[B]): IO[B] = IO { () => f(run()).run() }
    def map[B](f: A => B): IO[B] = flatMap(a => pure(f(a)))
    def flatten[B](implicit ev: A <:< IO[B]): IO[B] = flatMap(a => a)
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

    println(">> reify F[] with IO")
    val io: IO[BigInt] = computeF[IO](1, 4)

    val result = io.run()
    println(s"result = $result")
    //=> 6227020800

    Thread sleep 500L
  }

  def computeWithId(): Unit = {

    println(">> reify F[] with cats.Id")
    val result: cats.Id[BigInt] = computeF[cats.Id](1, 4)

    println(s"result = $result")
    //=> 6227020800

    Thread sleep 500L
  }

  def computeWithOption(): Unit = {

    import cats.instances.option._

    println(">> reify F[] with Option")
    val maybeResult: Option[BigInt] = computeF[Option](1, 4)

    maybeResult foreach { result => println(s"result = $result") }
    //=> 6227020800

    Thread sleep 500L
  }

  def computeWithFuture(): Unit = {

    import cats.instances.future._

    import scala.concurrent.{ExecutionContext, Future}
    import ExecutionContext.Implicits.global

    println(">> reify F[] with Future")
    val future: Future[BigInt] = computeF[Future](1, 4)

    future foreach { result => println(s"result = $result") }
    //=> 6227020800

    Thread sleep 500L
  }

  computeWithIO()
  computeWithId()
  computeWithOption()
  computeWithFuture()

  println("-----\n")
}
