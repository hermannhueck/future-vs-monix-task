package ch05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

object App04bTaskForeachWithError extends App {

  println(s"\n----- Main $currentThread")

  def compute: Int = throw new IllegalStateException("illegal state")

  val task: Task[Int] = Task {
    println(s"side effect in $currentThread")
    compute
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println

  Thread.sleep(100L)
  println("-----\n")
}