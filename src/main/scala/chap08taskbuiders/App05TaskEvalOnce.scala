package chap08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App05TaskEvalOnce extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.evalOnce {
    println(s"side effect in $currentThread")
    fibonacci(20)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println
  //=> side effect
  //=> 10946

  // Result was memoized on the first run!
  task runAsync printCallback
  //=> 10946

  Thread.sleep(100L)
  println("-----\n")
}
