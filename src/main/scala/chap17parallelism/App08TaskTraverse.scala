package chap17parallelism

import monix.eval.Task
import monix.execution.Scheduler

import scala.language.postfixOps

object App08TaskTraverse extends App {

  println(s"\n----- Main $currentThread")

  def task(i: Int): Task[Int] = Task { println("side effect  " + i); i }

  val taskOfSeq: Task[Seq[Int]] = Task.traverse(Seq(1, 2))(i => task(i))

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
