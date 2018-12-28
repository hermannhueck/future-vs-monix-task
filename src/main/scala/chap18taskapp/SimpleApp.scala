package chap18taskapp

import cats.syntax.functor._
import cats.effect.ExitCode
import monix.eval.{Task, TaskApp}

object SimpleApp extends TaskApp {

  def run(args: List[String]): Task[ExitCode] =

    args.headOption match {
      case Some(name) =>
        Task(println(s"Hello, $name.")).as(ExitCode.Success)
      case None =>
        Task(System.err.println("Usage: MyApp name")).as(ExitCode(2))
    }
}
