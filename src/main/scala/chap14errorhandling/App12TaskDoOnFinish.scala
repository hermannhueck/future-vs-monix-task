package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

object App12TaskDoOnFinish extends App {

  println(s"\n----- Main $currentThread")

  val task: Task[Int] = Task(1)

  val finishCallback: Option[Throwable] => Task[Unit] = {
    case None =>
      println("Was success!")
      Task.unit
    case Some(ex) =>
      println(s"Had failure: $ex")
      Task.unit
  }

  val withFinishCallback: Task[Int] = task doOnFinish finishCallback

  implicit val scheduler: Scheduler = Scheduler.global

  withFinishCallback.runToFuture foreach println
  //=> Was success!
  //=> 1

  Thread sleep 1000L
  println("-----\n")
}
