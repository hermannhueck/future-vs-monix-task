package chap17parallelism

import monix.eval.Task
import monix.execution.Scheduler

object App02WithParZip3 extends App {

  println(s"\n----- Main $currentThread")

  // Potentially executed in parallel
  val aggregate: Task[BigInt] =
    Task.parZip3(sumTask(0, 1000), factorialTask(10), fibonacciTask(10)).map {
      case (sum, fac, fib) => sum + fac + fib
    }

  implicit val scheduler: Scheduler = Scheduler.global

  aggregate runAsync printCallback

  Thread sleep 1000L
  println("-----\n")
}
