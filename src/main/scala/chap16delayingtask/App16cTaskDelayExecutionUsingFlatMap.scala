package chap16delayingtask

import monix.eval.Task

object App16cTaskDelayExecutionUsingFlatMap extends App {

  println("\n-----")

  val trigger = Task.unit.delayExecution(2.seconds)

  val source = Task {
    println("Side-effect!")
    "Hello, world!"
  }

  val delayed: Task[String] = trigger.flatMap(_ => source)
  delayed.runToFuture.foreach(println)

  Thread.sleep(3000L)
  println("-----\n")
}
