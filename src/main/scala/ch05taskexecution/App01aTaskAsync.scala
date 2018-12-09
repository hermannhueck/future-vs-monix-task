package ch05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

object App01aTaskAsync extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  val task: Task[Int] = Task {
    println("side effect")
    compute
  }

  val callback: Either[Throwable, Int] => Unit = {      // Either based callback
    case Right(result) => println(s"result = $result")
    case Left(ex) => println(s"ERROR: ${ex.getMessage}")
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync callback
  println(">>> Task started")

  Thread.sleep(100L)
  println("-----\n")
}
