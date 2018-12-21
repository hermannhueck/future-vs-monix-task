package chap11tailrecloops

import monix.eval.Task
import monix.execution.Scheduler

object App04MutualTailRecursion extends App {

  println(s"\n----- Main $currentThread")

  def odd(n: Int): Task[Boolean] =
    Task.eval(n == 0).flatMap {
      case true => Task.now(false)
      case false => if (n > 0) even(n - 1) else even(n + 1)
    }

  def even(n: Int): Task[Boolean] =
    Task.eval(n == 0).flatMap {
      case true => Task.now(true)
      case false => if (n > 0) odd(n - 1) else odd(n + 1)
    }

  val task: Task[Boolean] = even(-1000000)

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach println

  Thread.sleep(1000L)
  println("-----\n")
}
