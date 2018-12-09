package ch05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

object App04aTaskForeach extends App {

  println("\n-----")

  def compute: Int = 1 + 1

  val task: Task[Int] = Task {
    println("side effect")
    compute
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach { result => println(s"result = $result") }
  task foreach println

  task.runToFuture foreach { result => println(s"result = $result") }
  task.runToFuture foreach println

  Thread.sleep(100L)
  println("-----\n")
}
