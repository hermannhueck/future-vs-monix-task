package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

object App06bTaskOnErrorRecoverWith extends App {

  println(s"\n----- Main $currentThread")

  val source =
    Task(sumOfRange(1, 100))
      .delayExecution(10.seconds)
      .timeout(3.seconds)

  val recovered = source.onErrorRecoverWith {
    case _: TimeoutException =>
      // Oh, we know about timeouts, recover it
      Task.now("Recovered!")
  }

  implicit val scheduler: Scheduler = Scheduler.global

  recovered.runToFuture foreach println
  //=> Recovered!

  Thread sleep 4000L
  println("-----\n")
}
