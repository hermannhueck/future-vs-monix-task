package ch11monad

import monix.eval.Task
import monix.execution.Scheduler

object App03ForComprehension extends App {

  println(s"\n----- Main $currentThread")

  def sumTask(from: Int, to: Int) = Task { sumOfRange(from, to) }
  def fibonacciTask(num: Int) = Task { fibonacci(num) }
  def factorialTask(num: Int) = Task { factorial(num) }

  def computeTask(from: Int, to: Int): Task[BigInt] =
    for {
      x <- sumTask(from, to)            // 6
      y <- fibonacciTask(x)             // 13
      z <- factorialTask(y.intValue)    // 6227020800
    } yield z

  val task = computeTask(1, 4)

  import Scheduler.Implicits.global

  task runAsync printCallback
  //=> 6227020800

  Thread.sleep(100L)
  println("-----\n")
}
