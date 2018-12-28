import monix.eval.Task

package object chap17parallelism extends tools.Tools {

  def sumTask(from: Int, to: Int): Task[Int] =
    Task { sumOfRange(from, to) }

  def factorialTask(n: Int): Task[BigInt] =
    Task { factorial(n) }

  def fibonacciTask(cycles: Int): Task[BigInt] =
    Task { fibonacci(cycles) }
}
