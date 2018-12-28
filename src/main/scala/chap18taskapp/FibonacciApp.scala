package chap18taskapp

import cats.syntax.functor._
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}

import scala.util.Try

object FibonacciApp extends TaskApp {

  def run(args: List[String]): Task[ExitCode] =

    args.headOption match {

      case None =>
        Task(System.err.println("Usage: MyApp cycles")).as(ExitCode(1))

      case Some(cyclesStr) =>
        Try(cyclesStr.toInt).toEither match {
          case Left(t) =>
            Task(System.err.println("Not a positive number")).as(ExitCode(2))
          case Right(cycles) =>
            defineWork(cycles).as(ExitCode.Success)
        }
    }

  def defineWork(cycles: Int): Task[Unit] = for {
    result <- Task { fibonacci(cycles) }
    _ <- Task(println("\n-----"))
    _ <- Task(println(s"Fibonacci for $cycles cycles is: $result."))
    _ <- Task(println("-----\n"))
  } yield ()
}
