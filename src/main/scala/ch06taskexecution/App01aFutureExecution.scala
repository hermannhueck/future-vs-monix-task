package ch06taskexecution

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object App01aFutureExecution extends App {

  println(s"\n----- Main $currentThread")

  implicit val ec: ExecutionContext = ExecutionContext.global

  val task: Future[Int] = Future {
    println(s"Future running on $currentThread")
    sumOfRange(0, 1000)
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
