package ch05taskexecution

import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object App03bCancelableFuture extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  val task: Task[Int] = Task {
    println("side effect")
    compute
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
