package ch08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App08TaskEvalAsync extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.evalAsync {
    println(s"side effect in $currentThread")
    fibonacci(20)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  // The evaluation (and thus all contained side effects)
  // gets triggered on each run.
  // But it is run asynchronously on a different "logical" thread.
  task runAsync printCallback
  //=> side effect
  //=> 10946

  Thread.sleep(100L)
  println("-----\n")
}
