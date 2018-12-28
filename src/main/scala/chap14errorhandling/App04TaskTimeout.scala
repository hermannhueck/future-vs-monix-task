package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.language.postfixOps

object App04TaskTimeout extends App {

  println(s"\n----- Main $currentThread")

  val source: Task[Int] =
    Task(sumOfRange(1, 100))
      .delayExecution(10.seconds)

  // Triggers TimeoutException if the source does not
  // complete in 3 seconds after runAsync
  val timedOut: Task[Int] = source.timeout(3.seconds)

  implicit val scheduler: Scheduler = Scheduler.global

  timedOut runAsync println
  //=> Left(java.util.concurrent.TimeoutException: Task timed-out after 3 seconds of inactivity)

  Thread sleep 4000L
  println("-----\n")
}
