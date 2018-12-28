package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.TimeoutException
import scala.concurrent.duration._
import scala.language.postfixOps

object App05TaskTimeoutTo extends App {

  println(s"\n----- Main $currentThread")

  val source: Task[Int] =
    Task(sumOfRange(1, 100))
      .delayExecution(10.seconds)

  // Triggers Fallback Task if the source does not
  // complete in 3 seconds after runAsync
  val timedOut: Task[Int] = source.timeoutTo(
    3.seconds,
    Task.raiseError(new TimeoutException("That took too long!")) // Fallback Task
  )

  implicit val scheduler: Scheduler = Scheduler.global

  timedOut runAsync println
  //=> Left(java.util.concurrent.TimeoutException: That took too long!) // Fallback Task

  Thread sleep 4000L
  println("-----\n")
}
