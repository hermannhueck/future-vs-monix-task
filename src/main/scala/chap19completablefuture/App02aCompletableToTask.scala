package chap19completablefuture

import java.util.concurrent.CompletableFuture

import monix.eval.Task
import monix.execution.{FutureUtils, Scheduler}

import scala.concurrent.Future

object App02aCompletableToTask extends App {

  println(s"\n----- Main $currentThread")

  def completable: CompletableFuture[BigInt] =
    CompletableFuture.supplyAsync(() => fibonacci(6))

  def future: Future[BigInt] = FutureUtils.fromJavaCompletable(completable)

  val task = Task.deferFutureAction { implicit scheduler => future }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println

  Thread sleep 1000L
  println("-----\n")
}
