package chap16delayingtask

import monix.eval.Task

import scala.util.Random

object App17cTaskDelayResultUsingFlatMap extends App {

  println("\n-----")

  val source = Task {
    println("Side-effect!")
    Random.nextInt(10)
  }

  def selector(x: Int): Task[Int] =
    Task(x).delayExecution(x.seconds)

  val delayed: Task[Int] =
    source
      .delayExecution(1.second)
      .flatMap(x => selector(x))

  delayed.runToFuture.foreach { x =>
    println(s"Result: $x (signaled after at least ${x+1} seconds)")
  }


  Thread.sleep(11000L)
  println("-----\n")
}
