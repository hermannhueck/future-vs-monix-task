package ch05taskexecution

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object App01aFutureExecution extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  implicit val ec: ExecutionContext = ExecutionContext.global

  val task: Future[Int] = Future {
    compute
  }
  println(">>> Future started")

  val callback: Try[Int] => Unit = {                    // Try based callback
    case Success(result) => println(s"result = $result")
    case Failure(ex) => println(s"ERROR: ${ex.getMessage}")
  }

  task onComplete callback

  Thread.sleep(100L)
  println("-----\n")
}
