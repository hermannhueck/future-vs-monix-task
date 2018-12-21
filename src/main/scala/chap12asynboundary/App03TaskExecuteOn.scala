package chap12asynboundary

import monix.eval.Task
import monix.execution.Scheduler

object App03TaskExecuteOn extends App {

  println(s"\n----- Main $currentThread")
  //=> Main Thread: run-main-2a

  lazy val io = Scheduler.io(name="my-io")  // I/O scheduler

  val source = Task { println(s"Running on $currentThread") }

  val forked = source.executeOn(io)         // override default Scheduler by fork

  val onFinish = Task { println(s"Ending on $currentThread") }

  val task =
    source
      .flatMap(_ => forked)
      .doOnFinish(_ => onFinish)

  import Scheduler.Implicits.global

  task.runToFuture
  //=> Running on thread: run-main-2a
  //=> Running on thread: my-io-803
  //=> Ending on thread: scala-execution-context-global-800

  Thread sleep 100L
  println("-----\n")
}
