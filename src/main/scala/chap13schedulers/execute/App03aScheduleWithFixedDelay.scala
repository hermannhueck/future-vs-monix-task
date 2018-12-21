package chap13schedulers.execute

import monix.execution.Cancelable
import monix.execution.Scheduler.{global => scheduler}

import scala.concurrent.duration._

object App03aScheduleWithFixedDelay extends App {

  println(s"\n----- Main $currentThread")

  val cancelable: Cancelable = scheduler.scheduleWithFixedDelay(1.seconds, 3.seconds) {
    println(s"Running on $currentThread")
    println("Fixed delay task")
  }
  println("started")

  Thread sleep 10000L

  // In case we change our mind, before time's up
  cancelable.cancel()
  println("canceled")

  println("-----\n")
}
