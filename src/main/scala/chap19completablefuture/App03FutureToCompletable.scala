package chap19completablefuture

import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

import monix.execution.FutureUtils

import scala.concurrent.{ExecutionContext, Future}

object App03FutureToCompletable extends App {

  println(s"\n----- Main $currentThread")

  implicit val ec: ExecutionContext = ExecutionContext.global

  val future: Future[BigInt] = Future { fibonacci(6) }

  val completable: CompletableFuture[BigInt] = FutureUtils.toJavaCompletable(future)

  val consumer: Consumer[BigInt] = new Consumer[BigInt] {
    override def accept(value: BigInt): Unit = println(value)
  }
  completable.thenAccept(consumer)

  Thread sleep 1000L
  println("-----\n")
}
