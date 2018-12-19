package ch07memoization

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

object App03TaskMemoizeOnSuccess extends App {

  println(s"\n----- Main $currentThread")

  var effect = 0

  val source = Task.eval {
    println(s"side effect in $currentThread")
    effect += 1
    if (effect < 3) throw new RuntimeException("dummy") else effect
  }

  val cached = source.memoizeOnSuccess

  cached runAsync printCallback   //=> java.lang.RuntimeException: dummy
  cached runAsync printCallback   //=> java.lang.RuntimeException: dummy
  cached runAsync printCallback   //=> 3
  cached runAsync printCallback   //=> 3

  Thread.sleep(100L)
  println("-----\n")
}
