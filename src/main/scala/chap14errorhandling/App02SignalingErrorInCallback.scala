package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.language.postfixOps

object App02SignalingErrorInCallback extends App {

  // ----- Error in the runAsync-callback

  println(s"\n----- Main $currentThread")

  // Ensures asynchronous execution, just to show that
  // the action doesn't happen on the current thread.
  val task =
    Task(sumOfRange(1, 100))
      .delayExecution(1 second)

  implicit val scheduler: Scheduler = Scheduler.global

  task runAsync { r => throw new IllegalStateException(r.toString) }

  // After 1 second, this will log the whole stack trace:
  /*
  java.lang.IllegalStateException: Right(2)
	at monixdoc.evaluation.task.App02SignalingErrorInCallback.$anonfun$new$1(App02SignalingErrorInCallback.scala:19)
	at monix.execution.Callback$$anon$3.onSuccess(Callback.scala:145)
	at monix.eval.internal.TaskRunLoop$.startFull(TaskRunLoop.scala:143)
	at monix.eval.internal.TaskRestartCallback.syncOnSuccess(TaskRestartCallback.scala:108)
	at monix.eval.internal.TaskRestartCallback.onSuccess(TaskRestartCallback.scala:74)
	at monix.eval.internal.TaskSleep$SleepRunnable.run(TaskSleep.scala:62)
	at java.base/java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1426)
	at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:290)
	at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1020)
	at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1656)
	at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1594)
	at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
   */

  Thread sleep 2000L
  println("-----\n")
}
