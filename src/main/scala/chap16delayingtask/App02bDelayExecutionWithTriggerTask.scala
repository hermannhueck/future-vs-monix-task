package chap16delayingtask

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

object App02bDelayExecutionWithTriggerTask extends App {

  println(s"\n----- Main $currentThread")

  val trigger: Task[Unit] = Task.unit.delayExecution(2.seconds)

  val source = Task {
    println(s"side effect on $currentThread")
    "Hello"
  }

  import cats.syntax.apply._

  val delayed: Task[String] =
    trigger *> source

  implicit val scheduler: Scheduler = Scheduler.global

  delayed.runToFuture foreach println

  Thread sleep 3000L
  println("-----\n")
}
