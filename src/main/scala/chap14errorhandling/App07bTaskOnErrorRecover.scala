package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

import scala.concurrent.TimeoutException

object App07bTaskOnErrorRecover extends App {

  println(s"\n----- Main $currentThread")

  val source =
    Task(sumOfRange(1, 100))
      .delayExecution(10.seconds)
      .timeout(3.seconds)

  val recovered = source.onErrorRecover {
    case _: TimeoutException =>
      // Oh, we know about timeouts, recover it
      "Recovered!"
  }

  implicit val scheduler: Scheduler = Scheduler.global

  recovered.runToFuture foreach println
  //=> Recovered!

  Thread sleep 4000L
  println("-----\n")
}
