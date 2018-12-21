package chap09futuretotask

import monix.eval.Task
import monix.execution.Scheduler

object App04TaskDeferFutureAction extends App {

  println(s"\n----- Main $currentThread")

  import chap08taskbuiders.library._

  val task = Task.deferFutureAction(implicit scheduler => futureFactorial(10))

  import Scheduler.Implicits.global

  task foreach println
  //=> side effect
  //=> 3628800
  task foreach println
  //=> side effect
  //=> 3628800

  Thread.sleep(100L)
  println("-----\n")
}
