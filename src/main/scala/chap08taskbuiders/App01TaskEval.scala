package chap08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App01TaskEval extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.eval {
    println(s"side effect in $currentThread")
    fibonacci(20)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println
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
