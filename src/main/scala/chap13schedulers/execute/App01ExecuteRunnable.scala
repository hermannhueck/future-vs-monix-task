package chap13schedulers.execute

import monix.execution.Scheduler

object App01ExecuteRunnable extends App {

  println(s"\n----- Main $currentThread")
  //=> Main Thread: run-main-1

  Scheduler.global.execute(new Runnable {
    def run(): Unit = {
      println(s"Running on $currentThread")
      println("Hello, world!")
    }
  })
  //=> Running on Thread: scala-execution-context-global-117
  //=> Hello, world!

  Thread sleep 100L
  println("-----\n")
}
