package chap19completablefuture

import monix.eval.Task
import monix.execution.{FutureUtils, Scheduler}

object App04bTaskToCompletable extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[BigInt] = Task { fibonacci(6) }

  implicit val scheduler: Scheduler = Scheduler.global

  FutureUtils.toJavaCompletable(task.runToFuture).thenAccept { println }

  Thread sleep 1000L
  println("-----\n")
}
