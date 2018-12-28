package chap17parallelism

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

import scala.concurrent.duration._
import scala.language.postfixOps

object App07TaskGatherUnordered extends App {

  println(s"\n----- Main $currentThread")

  val task1 = Task { println("side effect 1"); 1 }.delayExecution(1 second)
  val task2 = Task { println("side effect 2"); 2 }.delayExecution(1 second)
  val seqOfTask: Seq[Task[Int]] = Seq(task1, task2)

  val taskOfSeq: Task[Seq[Int]] = Task.gatherUnordered(seqOfTask)

  implicit val scheduler: Scheduler = Scheduler.global

  // There’s potential for parallel execution:
  // Ordering of effects is not guaranteed, results in the List are unordered too.
  taskOfSeq foreach println
  //=> side effect 1
  //=> side effect 2
  //=> List(1, 2)

  taskOfSeq foreach println
  //=> side effect 2
  //=> side effect 1
  //=> List(2, 1)

  Thread sleep 3000L
  println("-----\n")
}
