package chap16delayingtask

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.util.Random

object App04bDelayResultWithSelectorTask extends App {

  println(s"\n----- Main $currentThread")

  val source = Task {
    println(s"side effect on $currentThread")
    Random.nextInt(5)
  }

  def selector(x: Int): Task[Unit] =
    Task.unit.delayExecution(x.seconds)

  val delayed: Task[Int] =
    source
      .delayExecution(1.second)
      .delayResultBySelector(x => selector(x))

  implicit val scheduler: Scheduler = Scheduler.global

  delayed.runToFuture.foreach { x =>
    println(s"Result: $x (signaled after at least ${x+1} seconds)")
  }

  Thread sleep 6000L
  println("-----\n")
}
