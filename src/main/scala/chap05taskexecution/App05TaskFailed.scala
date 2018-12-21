package chap05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

object App05TaskFailed extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task {
    // println(s"side effect in $currentThread")
    throw new IllegalStateException("illegal state")
  }

  val failed: Task[Throwable] = task.failed

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println
  // prints nothing
  failed foreach println
  // prints: java.lang.IllegalStateException: illegal state

  Thread.sleep(100L)
  println("-----\n")
}
