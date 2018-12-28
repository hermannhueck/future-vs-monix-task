package chap17parallelism

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.language.postfixOps

object App10TaskWanderUnordered extends App {

  println(s"\n----- Main $currentThread")

  def task(i: Int): Task[Int] =
    Task { println("side effect  " + i); i }.delayExecution(1 second)

  val taskOfSeq: Task[Seq[Int]] = Task.wanderUnordered(Seq(1, 2))(i => task(i))

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
