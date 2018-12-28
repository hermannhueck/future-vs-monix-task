package chap15parallelism

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

import scala.concurrent.duration._
import scala.language.postfixOps

object App14bTaskWander extends App {

  println("\n-----")

  def task(i: Int): Task[Int] = Task { println("Effect " + i); i }.delayExecution(1 second)

  val list: Task[Seq[Int]] = Task.wander(Seq(1, 2))(i => task(i))

  val cb = new Callback[Throwable, Seq[Int]] {
    override def onSuccess(value: Seq[Int]): Unit = println(value)
    override def onError(e: Throwable): Unit = println(e.toString)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  // There’s potential for parallel execution:
  // Ordering of effects is not guaranteed, but the results in the List are ordered.
  // list.runToFuture.foreach(println)
  list.runAsync(cb)
  //=> Effect1
  //=> Effect2
  //=> List(1, 2)

  // list.runToFuture.foreach(println)
  list.runAsync(cb)
  //=> Effect2
  //=> Effect1
  //=> List(1, 2)

  Thread.sleep(3000L)
  println("-----\n")
}
