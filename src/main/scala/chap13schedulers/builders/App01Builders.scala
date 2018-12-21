package chap13schedulers.builders

import java.util.concurrent.{Executors, ScheduledExecutorService}

import monix.execution.ExecutionModel.AlwaysAsyncExecution
import monix.execution.{ExecutionModel, Scheduler, UncaughtExceptionReporter}

import scala.concurrent.ExecutionContext

object App01Builders extends App {

  println(s"\n----- Main $currentThread")

  // Will schedule things with delays
  lazy val scheduledExecutor: ScheduledExecutorService =
    Executors.newSingleThreadScheduledExecutor()

  // For actual execution of tasks
  lazy val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  // Logs errors to stderr or something
  lazy val exceptionReporter: UncaughtExceptionReporter =
    UncaughtExceptionReporter(executionContext.reportFailure)

  lazy val executionModel: ExecutionModel = AlwaysAsyncExecution

  lazy val scheduler = Scheduler(
    scheduledExecutor,
    executionContext,
    exceptionReporter,
    executionModel
  )

  lazy val scheduler2 = Scheduler(scheduledExecutor, executionContext)

  lazy val scheduler3 = Scheduler(executionContext)

  lazy val scheduler4 = {
    val javaService: ScheduledExecutorService = Executors.newScheduledThreadPool(10)
    Scheduler(javaService)
  }

  lazy val scheduler5 = {
    val javaService = Executors.newScheduledThreadPool(10)
    Scheduler(javaService, AlwaysAsyncExecution)
  }

  // ForkJoinPool for CPU-bound tasks:

  // Simple constructor
  lazy val scheduler6 =
    Scheduler.computation(parallelism = 10)

  // Specify an optional ExecutionModel
  lazy val scheduler7 =
    Scheduler.computation(
      parallelism = 10,
      executionModel = AlwaysAsyncExecution
    )

  // unbounded thread-pool meant for I/O-bound tasks, backed by a Java CachedThreadPool:

  lazy val scheduler8 =
    Scheduler.io()

  // Giving it a name
  lazy val scheduler9 =
    Scheduler.io(name="my-io")

  // Explicit execution model
  lazy val scheduler10 =
    Scheduler.io(
      name="my-io",
      executionModel = AlwaysAsyncExecution
    )

  // emulating Javascript’s environment: single threaded thread-pool, backed by a Java SingleThreadScheduledExecutor:

  lazy val scheduler11 =
    Scheduler.singleThread(name = "my-thread")

  // fixed size thread pool, backed by a Java ScheduledThreadPool:

  lazy val scheduler12 =
    Scheduler.fixedPool(name = "my-fixed", poolSize = 10)

  // Builders for Javascript:

  lazy val scheduler13 =
    Scheduler(executionModel=AlwaysAsyncExecution)

  // with trampolining ...
  lazy val scheduler14 =
    Scheduler.trampoline()

  // with trampolining and explicit ExecutionModel
  lazy val scheduler15 =
    Scheduler.trampoline(executionModel=AlwaysAsyncExecution)

  println("-----\n")
}
