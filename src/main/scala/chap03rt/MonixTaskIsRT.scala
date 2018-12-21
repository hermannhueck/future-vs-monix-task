package chap03rt

import java.util.concurrent.atomic.AtomicInteger

import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

/*
  see blogpost:
  https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/
 */
object MonixTaskIsRT extends App {

  println("\n-----")

  val task1: Task[(Int, Int)] = {
    val atomicInt = new AtomicInteger(0)
    val task: Task[Int] = Task { atomicInt.incrementAndGet }
    for {
      x <- task
      y <- task
    } yield (x, y)
  }

  // same as future1, but inlined
  val task2: Task[(Int, Int)] = {
    val atomicInt = new AtomicInteger(0)
    for {
      x <- Task { atomicInt.incrementAndGet }
      y <- Task { atomicInt.incrementAndGet }
    } yield (x, y)
  }

  task1 runAsync println     // Success((1,2))
  task2 runAsync println     // Success((1,2))    <-- same result

  Thread.sleep(200L)
  println("-----")
  println("The results are equal. --> Monix Task IS referentially transparent.")
  println("-----\n")
}
