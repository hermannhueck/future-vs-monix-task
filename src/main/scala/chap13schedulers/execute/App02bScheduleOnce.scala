package chap13schedulers.execute

import java.util.concurrent.TimeUnit

import monix.execution.Cancelable
import monix.execution.Scheduler.{global => scheduler}

object App02bScheduleOnce extends App {

  println(s"\n----- Main $currentThread")

  val cancelable: Cancelable = scheduler.scheduleOnce(2, TimeUnit.SECONDS, new Runnable {
      def run(): Unit = {
        println(s"Running on $currentThread")
        println("Hello, world!")
      }
    })
  println("started")

  // In case we change our mind, before time's up
  cancelable.cancel()
  println("canceled")

  Thread sleep 2200L
  println("-----\n")
}
