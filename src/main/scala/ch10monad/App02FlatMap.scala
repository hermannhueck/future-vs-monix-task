package ch10monad

import monix.eval.Task
import monix.execution.Scheduler

object App02FlatMap extends App {

  println(s"\n----- Main $currentThread")

  def sumTask(from: Int, to: Int) = Task { sumOfRange(from, to) }
  def fibonacciTask(num: Int) = Task { fibonacci(num) }
  def factorialTask(num: Int) = Task { factorial(num) }

  def computeTask(from: Int, to: Int): Task[BigInt] =
    sumTask(from, to)                  // 6
      .flatMap(fibonacciTask)          // 13
      .map(_.intValue)                 // 13
      .flatMap(factorialTask)          // 6227020800

  val task = computeTask(1, 4)

  import Scheduler.Implicits.global

  task runAsync printCallback
  //=> 6227020800

  Thread.sleep(100L)
  println("-----\n")
}
