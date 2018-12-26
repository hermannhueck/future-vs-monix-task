package chap14errorhandling

import com.sun.org.slf4j.internal.LoggerFactory
import monix.eval.Task
import monix.execution.{Scheduler, UncaughtExceptionReporter}

import scala.concurrent.duration._
import scala.language.postfixOps

object App03OverridingErrorLogging extends App {

  println(s"\n----- Main $currentThread")

  val reporter = UncaughtExceptionReporter { ex =>
    val logger = LoggerFactory.getLogger(this.getClass) // log with SLF4J
    logger.error("Uncaught exception", ex)
  }

  implicit val global: Scheduler = Scheduler(Scheduler.global, reporter)

  val task =
    Task(sumOfRange(1, 100))
    .delayExecution(1 second)

  task.runAsync { r => throw new IllegalStateException(r.toString) }
  // logs Exception with SLF4J

  Thread sleep 2000L
  println("-----\n")
}
