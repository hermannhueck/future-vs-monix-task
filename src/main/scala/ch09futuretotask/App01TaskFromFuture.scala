package ch09futuretotask

import monix.eval.Task
import monix.execution.Scheduler

object App01TaskFromFuture extends App {

  println(s"\n----- Main $currentThread")

  import ch08taskbuiders.library._

  import Scheduler.Implicits.global

  // Future created before Task.fromFuture is invoked
  // Hence the Future is already running before we convert it to a Task
  val task = Task.fromFuture(futureFactorial(10))
  //=> side effect

  task foreach println
  //=> 3628800
  task foreach println
  //=> 3628800

  Thread.sleep(100L)
  println("-----\n")
}
