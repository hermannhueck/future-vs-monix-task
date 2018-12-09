package ch05taskexecution

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

object App02TaskAsyncWithCallback extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  val task: Task[Int] = Task {
    println("side effect")
    compute
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
