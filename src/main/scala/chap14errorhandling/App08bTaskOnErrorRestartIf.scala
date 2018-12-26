package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.util.Random

object App08bTaskOnErrorRestartIf extends App {

  println(s"\n----- Main $currentThread")

  val source = Task(Random.nextInt).flatMap {
    case even if even % 2 == 0 =>
      Task.now(even)
    case other =>
      Task.raiseError(new IllegalStateException(other.toString))
  }

  // Will keep retrying for as long as the source fails with an IllegalStateException
  val randomEven: Task[Int] = source.onErrorRestartIf {
    case _: IllegalStateException => true
    case _ => false
  }

  implicit val scheduler: Scheduler = Scheduler.global

  randomEven runAsync println

  Thread sleep 1000L
  println("-----\n")
}
