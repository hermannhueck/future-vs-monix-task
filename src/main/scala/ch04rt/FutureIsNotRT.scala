package ch04rt

import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/*
  see blogpost:
  https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/
 */
object FutureIsNotRT extends App {

  println("\n-----")

  val future1: Future[(Int, Int)] = {
    val atomicInt = new AtomicInteger(0)
    val future: Future[Int] = Future { atomicInt.incrementAndGet }
    for {
      x <- future
      y <- future
    } yield (x, y)
  }

  // same as future1, but inlined
  val future2: Future[(Int, Int)] = {
    val atomicInt = new AtomicInteger(0)
    for {
      x <- Future { atomicInt.incrementAndGet }
      y <- Future { atomicInt.incrementAndGet }
    } yield (x, y)
  }

  future1 onComplete println     // Success((1,1))
  future2 onComplete println     // Success((1,2))    <-- not the same result

  Thread.sleep(200L)
  println("-----")
  println("The results are not equal. --> Future IS NOT referentially transparent.")
  println("-----\n")
}
