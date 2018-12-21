package chap13schedulers.execute

import monix.execution.Cancelable
import monix.execution.Scheduler.{global => scheduler}

import scala.concurrent.duration._

object App02aScheduleOnce extends App {

  println(s"\n----- Main $currentThread")

  val cancelable: Cancelable = scheduler.scheduleOnce(2.seconds) {
    println(s"Running on $currentThread")
    println("Hello, world!")
  }
  println("started")

  // In case we change our mind, before time's up
  cancelable.cancel()
  println("canceled")

  Thread sleep 2200L
  println("-----\n")
}
