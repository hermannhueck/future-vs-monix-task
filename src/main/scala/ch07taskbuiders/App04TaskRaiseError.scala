package ch07taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App04TaskRaiseError extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.raiseError {     // Task.raiseError evaluates eagerly
    println(s"side effect in $currentThread") // NO SIDE EFFECTS in Task.now, only pure values !!
    new IllegalStateException("illegal state")
  }
  //=> side effect

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> java.lang.IllegalStateException: illegal state

  Thread.sleep(100L)
  println("-----\n")
}
