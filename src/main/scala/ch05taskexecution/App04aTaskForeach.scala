package ch05taskexecution

import monix.eval.Task
import monix.execution.Scheduler

object App04aTaskForeach extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task {
    println(s"side effect in $currentThread")
    sumOfRange(0, 1000)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach { result => println(s"result = $result") }
  task foreach println

  task.runToFuture foreach { result => println(s"result = $result") }
  task.runToFuture foreach println

  Thread.sleep(100L)
  println("-----\n")
}
