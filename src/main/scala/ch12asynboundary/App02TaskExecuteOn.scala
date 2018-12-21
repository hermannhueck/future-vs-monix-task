package ch12asynboundary

import monix.eval.Task
import monix.execution.Scheduler

object App02TaskExecuteOn extends App {

  println(s"\n----- Main $currentThread")
  //=> Main Thread: run-main-9

  lazy val io = Scheduler.io(name="my-io")  // I/O scheduler

  val source = Task { println(s"Running on $currentThread") }

  val forked = source.executeOn(io)         // override default Scheduler by fork

  import Scheduler.Implicits.global

  source.runToFuture
  //=> Running on Thread: run-main-9
  forked.runToFuture
  //=> Running on Thread: my-io-265

  Thread sleep 100L
  println("-----\n")
}
