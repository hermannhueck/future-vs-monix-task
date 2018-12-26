package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.util.Random

object App08aTaskOnErrorRestart extends App {

  println(s"\n----- Main $currentThread")

  val source = Task(Random.nextInt).flatMap {
    case even if even % 2 == 0 =>
      Task.now(even)
    case other =>
      Task.raiseError(new IllegalStateException(other.toString))
  }

  // Will retry 2 times for a random even number, or fail if the maxRetries is reached!
  val randomEven: Task[Int] = source.onErrorRestart(maxRetries = 3)

  implicit val scheduler: Scheduler = Scheduler.global

  randomEven runAsync println

  Thread sleep 1000L
  println("-----\n")
}
