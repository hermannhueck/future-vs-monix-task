package chap11tailrecloops

import monix.eval.Task
import monix.execution.Scheduler

object App02TailrecWithTask extends App {

  println(s"\n----- Main $currentThread")

  def fibonacciTask(cycles: Int, x: BigInt = 0, y: BigInt = 1): Task[BigInt] =
    if (cycles > 0)
      Task.defer(fibonacciTask(cycles-1, y, x+y))
    else
      Task.now(y)

  val task: Task[BigInt] = fibonacciTask(6)

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println    // fibonacci computation starts here

  Thread.sleep(100L)
  println("-----\n")
}
