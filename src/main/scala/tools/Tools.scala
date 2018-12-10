package tools

import monix.execution.Callback

import scala.annotation.tailrec

trait Tools {

  final def sumOfRange(from: Int, to: Int): Int =
    (from until to).toList.sum

  @tailrec
  final def fibonacci(cycles: Int, a: BigInt = 0, b: BigInt = 1): BigInt = {
    if (cycles > 0)
      fibonacci(cycles - 1, b, a + b)
    else
      b
  }

  def factorial(n: Int): Int = {

    @tailrec
    def fac(n2: Int, acc: Int): Int =
      if (n2 == 0)
        acc
      else
        fac(n2 - 1, n2 * acc)

    if (n < 0)
      throw new IllegalArgumentException("factorial of $n not defined")
    else
      fac(n, 1)
  }

  def currentThread: String = "Thread: " + Thread.currentThread.getName

  def printCurrentThread(): Unit = println(currentThread)


  def printCallback[A]: Callback[Throwable, A] = new Callback[Throwable, A] {

    def onSuccess(result: A): Unit = println(s"result = $result")

    def onError(ex: Throwable): Unit = println(s"ERROR: ${ex.toString}")
  }
}
