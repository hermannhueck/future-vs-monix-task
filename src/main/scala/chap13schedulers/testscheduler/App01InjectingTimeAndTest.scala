package chap13schedulers.testscheduler

import chap13schedulers.execute.currentThread
import monix.execution.schedulers.TestScheduler

import scala.concurrent.duration._

object App01InjectingTimeAndTest extends App {

  println(s"\n----- Main $currentThread")

  val testScheduler = TestScheduler()

  testScheduler.execute(new Runnable {
    def run(): Unit = {
      println(s"Running on $currentThread")
      println("Immediate!")
    }
  })

  testScheduler.scheduleOnce(1.second) {
    println(s"Running on $currentThread")
    println("Delayed execution!")
  }

  Thread.sleep(2000L)

  // Now we can fake it. Executes immediate tasks,
  // on the current thread:
  testScheduler.tick()
  // => Immediate!

  Thread.sleep(2000L)

  // Simulate passage of time, current thread:
  testScheduler.tick(5.seconds)
  // => Delayed execution!

  println("-----\n")
}
