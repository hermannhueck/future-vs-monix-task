package chap19completablefuture

import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

import monix.execution.FutureUtils

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

object App01CompletableToFuture extends App {

  println(s"\n----- Main $currentThread")

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val supplier: Supplier[BigInt] = () => fibonacci(6)

  def completable: CompletableFuture[BigInt] = CompletableFuture.supplyAsync(supplier)

  val future: Future[BigInt] = FutureUtils.fromJavaCompletable(completable)

  future foreach println

  Thread sleep 1000L
  println("-----\n")
}
