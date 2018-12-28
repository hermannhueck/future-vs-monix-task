package chap15parallelism

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

object App13aTaskSequence extends App {

  println("\n-----")

  val ta = Task { println("Effect1"); 1 }
  val tb = Task { println("Effect2"); 2 }

  val list: Task[Seq[Int]] = Task.sequence(Seq(ta, tb))

  val cb = new Callback[Throwable, Seq[Int]] {
    override def onSuccess(value: Seq[Int]): Unit = println(value)
    override def onError(e: Throwable): Unit = println(e.toString)
  }

  implicit val scheduler: Scheduler = Scheduler.global

  // We always get this ordering:
  // list.runToFuture.foreach(println)
  list.runAsync(cb)
  //=> Effect1
  //=> Effect2
  //=> List(1, 2)

  // Thread.sleep(1000L)
  println("-----\n")
}
