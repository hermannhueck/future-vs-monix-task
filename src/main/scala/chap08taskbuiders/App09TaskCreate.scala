package chap08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._
import scala.util.Try

object App09TaskCreate extends App {

  println("\n-----")

  def evalDelayed[A](delay: FiniteDuration)(f: => A): Task[A] = {

    // On execution, we have the scheduler and
    // the callback injected ;-)
    Task.create { (scheduler, callback) => // same as Task.async
      val cancelable =
        scheduler.scheduleOnce(delay) {
          callback(Try(f))
        }

      // We must return something that can
      // cancel the async computation
      cancelable
    }
  }

  val task = evalDelayed(3.seconds)(println("Delayed"))

  task.runAsync(r => println(r))

  Thread.sleep(4000L)
  println("-----\n")
}
