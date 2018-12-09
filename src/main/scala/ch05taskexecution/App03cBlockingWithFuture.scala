package ch05taskexecution

import monix.eval.Task
import monix.execution.{CancelableFuture, Scheduler}

import scala.concurrent.Await
import scala.concurrent.duration._

object App03cBlockingWithFuture extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  val task: Task[Int] = Task {
    println("side effect")
    compute
  }.delayExecution(1.second)

  implicit val scheduler: Scheduler = Scheduler.global

  println(">>> Starting task ...")
  val future: CancelableFuture[Int] = task.runToFuture

  println(">>> Blocking for result ...")
  val result = Await.result(future, 3.seconds)
  println(s"result = $result")

  Thread.sleep(100L)
  println("-----\n")
}
