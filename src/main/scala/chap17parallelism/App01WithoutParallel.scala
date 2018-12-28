package chap17parallelism

import monix.eval.Task
import monix.execution.Scheduler

object App01WithoutParallel extends App {

  println(s"\n----- Main $currentThread")

  // Sequential operations based on flatMap ...
  val aggregate: Task[BigInt] = for {
    sum <- sumTask(0, 1000)
    fac <- factorialTask(10)
    fib <- fibonacciTask(10)
  } yield sum + fac + fib

  implicit val scheduler: Scheduler = Scheduler.global

  aggregate runAsync printCallback

  Thread sleep 1000L
  println("-----\n")
}
