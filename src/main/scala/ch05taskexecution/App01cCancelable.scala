package ch05taskexecution

import monix.eval.Task
import monix.execution.{Cancelable, Scheduler}

import scala.concurrent.duration._

object App01cCancelable extends App {

  println("\n-----")

  val task: Task[Int] = Task {
    println("side effect")
    sumOfRange(0, 1000)
  }.delayExecution(1.second)

  val callback: Either[Throwable, Int] => Unit = {
    case Right(result) => println(s"result = $result")
    case Left(ex) => println(s"ERROR: ${ex.getMessage}")
  }

  implicit val scheduler: Scheduler = Scheduler.global

  val cancelable: Cancelable = task runAsync callback
  println(">>> Task started")

  // If we change our mind...
  cancelable.cancel()
  println(">>> Task canceled")

  Thread.sleep(100L)
  println("-----\n")
}
