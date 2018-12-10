package ch07taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App02TaskNow extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.now {     // Task.now evaluates eagerly
    println(s"side effect in $currentThread") // NO SIDE EFFECTS in Task.now, only pure values !!
    42
  }
  //=> side effect

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync printCallback
  //=> 42

  Thread.sleep(100L)
  println("-----\n")
}
