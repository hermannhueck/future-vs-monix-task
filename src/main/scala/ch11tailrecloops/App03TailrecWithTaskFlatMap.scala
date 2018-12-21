package ch11tailrecloops

import monix.eval.Task
import monix.execution.Scheduler

object App03TailrecWithTaskFlatMap extends App {

  println(s"\n----- Main $currentThread")

  def fib(cycles: Int, x: BigInt = 0, y: BigInt = 1): Task[BigInt] =
    Task.eval(cycles > 0).flatMap {
      case true =>
        fib(cycles-1, y, x+y)
      case false =>
        Task.now(y)
    }

  val task = fib(6)

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println    // fibonacci computation starts here

  Thread.sleep(1000L)
  println("-----\n")
}
