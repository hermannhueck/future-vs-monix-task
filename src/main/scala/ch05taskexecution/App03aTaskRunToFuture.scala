package ch05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object App03aTaskRunToFuture extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  val task: Task[Int] = Task {
    println("side effect")
    compute
  }

  implicit val scheduler: Scheduler = Scheduler.global

  val future: Future[Int] = task.runToFuture
  println(">>> Task started")

  val callback: Try[Int] => Unit = {
    case Success(result) => println(s"result = $result")
    case Failure(ex) => println(s"ERROR: ${ex.getMessage}")
  }

  future onComplete callback

  Thread.sleep(100L)
  println("-----\n")
}
