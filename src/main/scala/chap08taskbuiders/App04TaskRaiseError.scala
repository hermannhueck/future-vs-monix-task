package chap08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App04TaskRaiseError extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.raiseError {     // Task.raiseError evaluates eagerly
    println(s"(eagerly produced) side effect in $currentThread; DON'T DO THAT !!")
    new IllegalStateException("illegal state")
  }
  //=> side effect

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> java.lang.IllegalStateException: illegal state

  Thread.sleep(100L)
  println("-----\n")
}
