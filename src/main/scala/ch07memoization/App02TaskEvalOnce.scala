package ch07memoization

import monix.eval.Task
import monix.execution.Scheduler

object App02TaskEvalOnce extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.evalOnce {
    println(s"(eagerly produced) side effect in $currentThread; DON'T DO THAT !!")
    fibonacci(20)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> side effect
  //=> 10946

  // Result was memoized on the first run!
  task runAsync printCallback
  //=> 10946

  Thread.sleep(100L)
  println("-----\n")
}
