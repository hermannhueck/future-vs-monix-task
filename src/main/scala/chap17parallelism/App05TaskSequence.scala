package chap17parallelism

import monix.eval.Task
import monix.execution.{Callback, Scheduler}

object App05TaskSequence extends App {

  println(s"\n----- Main $currentThread")

  val task1 = Task { println("side effect 1"); 1 }
  val task2 = Task { println("side effect 2"); 2 }
  val seqOfTask: Seq[Task[Int]] = Seq(task1, task2)

  val taskOfSeq: Task[Seq[Int]] = Task.sequence(seqOfTask)

  implicit val scheduler: Scheduler = Scheduler.global

  // We always get the same ordering in the output:
  taskOfSeq foreach println
  //=> side effect 1
  //=> side effect 2
  //=> List(1, 2)

  taskOfSeq foreach println
  //=> side effect 1
  //=> side effect 2
  //=> List(1, 2)

  Thread sleep 1000L
  println("-----\n")
}
