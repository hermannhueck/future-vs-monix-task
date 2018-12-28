package chap17parallelism

import monix.eval.Task
import monix.execution.Scheduler

object App04WithParMapN extends App {

  println(s"\n----- Main $currentThread")

  import cats.syntax.parallel._

  // Potentially executed in parallel
  val aggregate: Task[BigInt] =
    (sumTask(0, 1000), factorialTask(10), fibonacciTask(10)) parMapN {
      _ + _ + _
    }

  implicit val scheduler: Scheduler = Scheduler.global

  aggregate runAsync printCallback

  Thread sleep 1000L
  println("-----\n")
}
