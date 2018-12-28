package chap16delayingtask

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._

object App02cDelayExecutionWithTriggerTask extends App {

  println(s"\n----- Main $currentThread")

  val trigger = Task.unit.delayExecution(2.seconds)

  val source = Task {
    println(s"side effect on $currentThread")
    "Hello"
  }

  val delayed: Task[String] = source.delayExecutionWith(trigger) // deprecated: use flatMap instead

  implicit val scheduler: Scheduler = Scheduler.global

  delayed.runToFuture foreach println

  Thread sleep 3000L
  println("-----\n")
}
