package ch08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

object App06TaskNever extends App {

  println(s"\n----- Main $currentThread")

  // A Task instance that never completes
  val never: Task[Int] = Task.never[Int]

  val timedOut: Task[Int] =
        never.timeoutTo(3.seconds, Task.raiseError(new TimeoutException))

  implicit val scheduler: Scheduler = Scheduler.global

  timedOut runAsync println
  // After 3 seconds:
  // => Left(java.util.concurrent.TimeoutException)

  Thread.sleep(4000L)
  println("-----\n")
}
