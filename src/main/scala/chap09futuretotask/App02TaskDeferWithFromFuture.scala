package chap09futuretotask

import monix.eval.Task
import monix.execution.Scheduler

object App02TaskDeferWithFromFuture extends App {

  println(s"\n----- Main $currentThread")

  import library._

  import Scheduler.Implicits.global

  val task = Task.defer {
    Task.fromFuture(futureFactorial(10))
  }

  task foreach println
  //=> side effect
  //=> 3628800
  task foreach println
  //=> side effect
  //=> 3628800

  Thread.sleep(100L)
  println("-----\n")
}
