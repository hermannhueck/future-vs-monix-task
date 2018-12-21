package chap10monad

import cats.Monad
import monix.eval.Task
import monix.execution.Scheduler
import cats.syntax.functor._
import cats.syntax.flatMap._

import scala.language.higherKinds

object App04Monad extends App {

  println(s"\n----- Main $currentThread")

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


  def computeWithTask(): Unit = {

    // reify F[] with Task
    val task: Task[BigInt] = computeF[Task](1, 4)

    import Scheduler.Implicits.global

    task runAsync printCallback
    //=> 6227020800

    Thread.sleep(100L)
  }

  def computeWithId(): Unit = {

    import cats.Id

    // reify F[] with Id
    val result: Id[BigInt] = computeF[Id](1, 4)

    println(s"result = $result")
    //=> 6227020800
  }

  def computeWithOption(): Unit = {

    import cats.instances.option._

    // reify F[] with Option
    val maybeResult: Option[BigInt] = computeF[Option](1, 4)

    maybeResult foreach { result => println(s"result = ${result}") }
    //=> 6227020800
  }

  def computeWithFuture(): Unit = {

    import scala.concurrent.Future
    import scala.concurrent.ExecutionContext.Implicits.global
    import cats.instances.future._

    // reify F[] with Future
    val task: Future[BigInt] = computeF[Future](1, 4)

    task foreach { result => println(s"result = $result")}
    //=> 6227020800

    Thread.sleep(100L)
  }

  computeWithTask()
  computeWithId()
  computeWithOption()
  computeWithFuture()

  println("-----\n")
}
