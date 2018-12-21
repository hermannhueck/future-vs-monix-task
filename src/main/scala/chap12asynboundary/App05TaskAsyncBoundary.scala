package chap12asynboundary

import monix.eval.Task
import monix.execution.Scheduler

object App05TaskAsyncBoundary extends App {

  println(s"\n----- Main $currentThread")
  //=> Main Thread: run-main-e

  lazy val io = Scheduler.io(name="my-io")  // I/O scheduler

  val source = Task { println(s"Running on $currentThread") }

  val forked = source.executeOn(io)         // override default Scheduler by fork

  val onFinish = Task { println(s"Ending on $currentThread") }

  val task =
    source // executes on global
      .flatMap(_ => forked) // executes on io
      .asyncBoundary // switch back to global
      .doOnFinish(_ => onFinish) // executes on global

  import Scheduler.Implicits.global

  task.runToFuture
  //=> Running on Thread: run-main-e
  //=> Running on Thread: my-io-358
  //=> Ending on Thread: scala-execution-context-global-343

  Thread sleep 100L
  println("-----\n")
}
