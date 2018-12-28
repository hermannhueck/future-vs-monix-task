package chap16delayingtask

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

object App01DelayExecutionForDuration extends App {

  println(s"\n----- Main $currentThread")

  val source = Task {
    println(s"side effect on $currentThread")
    "Hello"
  }

  val delayed: Task[String] =
    source
      .delayExecution(2.seconds)

  implicit val scheduler: Scheduler = Scheduler.global

  delayed.runToFuture foreach println

  Thread sleep 3000L
  println("-----\n")
}
