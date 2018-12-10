package ch05taskexecution

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

object App02TaskAsyncWithCallback extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task {
    println(s"side effect in $currentThread")
    sumOfRange(0, 1000)
  }

  val callback: Either[Throwable, Int] => Unit = new Callback[Throwable, Int] {
    def onSuccess(result: Int): Unit = println(s"result = $result")
    def onError(ex: Throwable): Unit = println(s"ERROR: ${ex.getMessage}")
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync callback
  println(">>> Task started")

  Thread.sleep(100L)
  println("-----\n")
}
