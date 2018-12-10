package ch07taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App08TaskEvalAsync extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.evalAsync {
    println(s"side effect in $currentThread")
    fibonacci(20)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> side effect
  //=> 10946

  Thread.sleep(100L)
  println("-----\n")
}
