package chap08taskbuiders

import monix.eval.Task
import monix.execution.Scheduler

object App03TaskUnit extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Unit] = Task.unit

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync println
  //=> Right(())

  Thread.sleep(100L)
  println("-----\n")
}
