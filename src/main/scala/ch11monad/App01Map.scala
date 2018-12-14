package ch11monad

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

import scala.io.Source

object App01Map extends App {

  println(s"\n----- Main $currentThread")

  import Scheduler.Implicits.global
  import ch09taskbuiders.library._

  val task: Task[List[(String, Int)]] =
    Task.eval {     // Attention! This Task doesn't close the resource "README.md"
      Source.fromURL("file:./README.md")
    } map {
      _.getLines
    } map {
      _.toList
    } map {
      wordCount(limit = 2)
    }

  task runAsync new Callback[Throwable, List[(String, Int)]] {

    def onError(ex: Throwable): Unit =
      println(s"ERROR: ${ex.toString}")
    def onSuccess(wcList: List[(String, Int)]): Unit =
      wcList foreach { case (word, count) => println(s"$word: $count")}
  }

  Thread.sleep(100L)
  println("-----\n")
}
