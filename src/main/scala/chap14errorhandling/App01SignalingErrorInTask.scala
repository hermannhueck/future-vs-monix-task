package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.util.Random

object App01SignalingErrorInTask extends App {

  // ----- Error in task definition

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task(Random.nextInt).flatMap {
    case even if even % 2 == 0 =>
      Task.now(even)
    case odd =>
      throw new IllegalStateException(odd.toString)   // Error in task definition
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync println
  //=> Right(-924040280)

  task runAsync println
  //=> Left(java.lang.IllegalStateException: 834919637)

  Thread sleep 1000L
  println("-----\n")
}
