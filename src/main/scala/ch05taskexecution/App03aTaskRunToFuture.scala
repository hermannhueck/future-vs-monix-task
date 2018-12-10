package ch05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object App03aTaskRunToFuture extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task {
    println(s"side effect in $currentThread")
    sumOfRange(0, 1000)
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
