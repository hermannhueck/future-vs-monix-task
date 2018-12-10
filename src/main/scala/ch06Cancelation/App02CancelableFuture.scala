package ch06Cancelation

import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

import scala.util.{Failure, Success, Try}

import scala.concurrent.duration._

object App02CancelableFuture extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task {
    println(s"side effect in $currentThread")
    sumOfRange(0, 1000)
  }.delayExecution(1.second)

  implicit val scheduler: Scheduler = Scheduler.global

  val future: CancelableFuture[Int] = task.runToFuture
  println(">>> Task started")

  val callback: Try[Int] => Unit = {
    case Success(result) => println(s"result = $result")
    case Failure(ex) => println(s"ERROR: ${ex.getMessage}")
  }

  future onComplete callback

  // If we change our mind...
  future.cancel()
  println(">>> Task canceled")

  Thread.sleep(100L)
  println("-----\n")
}
