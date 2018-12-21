package ch12asynboundary

import monix.eval.Task
import monix.execution.Scheduler

object App04TaskAsyncBoundary extends App {

  println(s"\n----- Main $currentThread")

  lazy val io = Scheduler.io(name="my-io")  // I/O scheduler

  val source = Task { println(s"Running on $currentThread") }

  val forked = source.executeOn(io)         // override default Scheduler by fork

  val asyncBoundary = Task.unit.executeAsync

  val onFinish = Task { println(s"Ending on $currentThread") }

  val task =
    source // executes on global
      .flatMap(_ => forked) // executes on io
      .flatMap(_ => asyncBoundary) // switch back to global
      .doOnFinish(_ => onFinish) // executes on global

  import Scheduler.Implicits.global

  task.runToFuture

  //=> Running on thread: run-main-0 // ForkJoinPool-1-worker-5
  //=> Running on thread: my-io-2
  //=> Ends on thread: ForkJoinPool-1-worker-5

  Thread sleep 100L
  println("-----\n")
}
