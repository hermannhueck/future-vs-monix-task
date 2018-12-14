package ch09taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App07TaskDefer extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.defer {
    println(s"side effect in $currentThread")
    Task.now(42)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> side effect
  //=> 10946

  // The evaluation (and thus all contained side effects)
  // gets triggered on each run:
  task runAsync printCallback
  //=> side effect
  //=> 10946

  Thread.sleep(100L)
  println("-----\n")
}
