package chap19completablefuture

import java.util.concurrent.CompletableFuture

import monix.eval.Task
import monix.execution.{FutureUtils, Scheduler}

object App04aTaskToCompletable extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[BigInt] = Task { fibonacci(6) }

  implicit val scheduler: Scheduler = Scheduler.global

  val completable: CompletableFuture[BigInt] = FutureUtils.toJavaCompletable(task.runToFuture)

  completable.thenAccept((value: BigInt) => println(value))

  Thread sleep 1000L
  println("-----\n")
}
