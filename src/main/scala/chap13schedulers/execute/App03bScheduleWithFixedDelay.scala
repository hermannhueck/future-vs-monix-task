package chap13schedulers.execute

import java.util.concurrent.TimeUnit

import monix.execution.Cancelable
import monix.execution.Scheduler.{global => scheduler}

object App03bScheduleWithFixedDelay extends App {

  println(s"\n----- Main $currentThread")

  val cancelable: Cancelable = scheduler.scheduleWithFixedDelay(
    1, 3, TimeUnit.SECONDS,
    new Runnable {
      def run(): Unit = {
        println(s"Running on $currentThread")
        println("Fixed delay task")
      }
    })
  println("started")

  Thread sleep 10000L

  // In case we change our mind, before time's up
  cancelable.cancel()
  println("canceled")

  println("-----\n")
}
