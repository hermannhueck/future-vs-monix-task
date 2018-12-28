package chap16delayingtask

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

object App03DelayResult extends App {

  println(s"\n----- Main $currentThread")

  val source = Task {
    println(s"side effect on $currentThread")
    "Hello"
  }

  val delayed: Task[String] =
    source
      .delayExecution(2.second)
      .delayResult(3.seconds)

  implicit val scheduler: Scheduler = Scheduler.global

  delayed.runToFuture.foreach(println)

  Thread sleep 6000L
  println("-----\n")
}
