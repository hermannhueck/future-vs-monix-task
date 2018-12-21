package chap08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App02TaskNow extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.now {     // Task.now evaluates eagerly
    println(s"(eagerly produced) side effect in $currentThread; DON'T DO THAT !!")
    42
  }
  //=> side effect

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> 42

  Thread.sleep(100L)
  println("-----\n")
}
