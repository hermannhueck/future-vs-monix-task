package chap19completablefuture

import java.util.concurrent.CompletableFuture

import monix.eval.Task
import monix.execution.{FutureUtils, Scheduler}

object App02bCompletableToTask extends App {

  println(s"\n----- Main $currentThread")

  val task = Task.deferFutureAction { implicit scheduler =>
    FutureUtils.fromJavaCompletable {
      CompletableFuture.supplyAsync(() => fibonacci(6))
    }
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println

  Thread sleep 1000L
  println("-----\n")
}
