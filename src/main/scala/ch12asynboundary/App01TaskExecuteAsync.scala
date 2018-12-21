package ch12asynboundary

import monix.eval.Task
import monix.execution.Scheduler

object App01TaskExecuteAsync extends App {

  println(s"\n----- Main $currentThread")
  //=> Main Thread: run-main-8

  // Task#executeAsync ensures an asynchronous boundary,
  // forcing the fork into another thread on execution.
  val task = Task.eval {
    println(s"Running on $currentThread")
    factorial(10)
  }.executeAsync

  import Scheduler.Implicits.global

  task.runToFuture foreach println
  //=> Running on Thread: scala-execution-context-global-241
  //=> 3628800

  Thread sleep 100L
  println("-----\n")
}
