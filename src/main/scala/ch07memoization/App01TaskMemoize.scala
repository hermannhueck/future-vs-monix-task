package ch07memoization

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

object App01TaskMemoize extends App {

  println(s"\n----- Main $currentThread")

  val task = Task {
    println(s"side effect in $currentThread")
    fibonacci(20)
  }

  val memoized = task.memoize

  memoized runAsync printCallback
  //=> side effect
  //=> 10946

  // Result was memoized on the first run!
  memoized runAsync printCallback
  //=> 10946

  Thread.sleep(100L)
  println("-----\n")
}
