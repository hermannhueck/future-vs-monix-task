package chap16delayingtask

import monix.eval.Task

object App16aTaskDelayExecution extends App {

  println("\n-----")

  val source = Task {
    println("Side-effect!")
    "Hello, world!"
  }

  val delayed: Task[String] = source.delayExecution(2.seconds)
  delayed.runToFuture.foreach(println)

  Thread.sleep(3000L)
  println("-----\n")
}
