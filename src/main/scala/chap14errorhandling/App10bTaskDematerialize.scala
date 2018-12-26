package chap14errorhandling

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.util.Try

object App10bTaskDematerialize extends App {

  println(s"\n----- Main $currentThread")

  val source: Task[Int] = Task.raiseError[Int](new IllegalStateException)

  // Exposing errors
  val materialized: Task[Try[Int]] = source.materialize

  // Hiding errors again
  val dematerialized: Task[Int] = materialized.dematerialize

  implicit val scheduler: Scheduler = Scheduler.global

  dematerialized runAsync new Callback[Throwable, Int] {
    def onSuccess(result: Int): Unit = println(s"result = $result")
    def onError(ex: Throwable): Unit = println(s"ERROR: ${ex.toString}")
  }
  //=> ERROR: java.lang.IllegalStateException

  Thread sleep 1000L
  println("-----\n")
}
