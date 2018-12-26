package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.util.{Failure, Success, Try}

object App10aTaskMaterialize extends App {

  println(s"\n----- Main $currentThread")

  val source: Task[Int] = Task.raiseError[Int](new IllegalStateException)

  val materialized: Task[Try[Int]] = source.materialize

  // Now we can flatMap over both success and failure:
  val recovered = materialized.flatMap {
    case Success(value) => Task.now(value)
    case Failure(_) => Task.now(0)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  recovered.runToFuture foreach println
  //=> 0

  Thread sleep 1000L
  println("-----\n")
}
