package chap09futuretotask

import monix.eval.Task
import monix.execution.Scheduler

object App03TaskDeferFuture extends App {

  println(s"\n----- Main $currentThread")

  import chap08taskbuiders.library._

  import Scheduler.Implicits.global

  val task = Task.deferFuture(futureFactorial(10))

  task foreach println
  //=> side effect
  //=> 3628800
  task foreach println
  //=> side effect
  //=> 3628800

  Thread.sleep(100L)
  println("-----\n")
}
