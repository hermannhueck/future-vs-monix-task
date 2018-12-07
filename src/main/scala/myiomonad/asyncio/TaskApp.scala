package myiomonad.asyncio

import monix.execution.Scheduler.Implicits.global
//import monix.eval.Task
import myiomonad.asyncio.task.Task

import scala.util.{Failure, Success, Try}

object TaskApp extends App {

  val millisToSleep = 500L

  {
    println("----- Task.eval")
    val task = Task.eval { println("!!! side effect"); 1 + 1 }

    println(task.runToTry)
    println(task.runToEither)
    runToFuture(task)
    runOnComplete(task)
    runAsync(task)
    Thread.sleep(millisToSleep)
  }

  {
    println("----- Task.now")
    val task = Task.now { println("!!! side effect"); 1 + 1 }

    println(task.runToTry)
    println(task.runToEither)
    runToFuture(task)
    runOnComplete(task)
    runAsync(task)
    Thread.sleep(millisToSleep)
  }

  {
    println("----- Task.raiseError")
    val task = Task.raiseError { println("!!! side effect"); new IllegalStateException("illegal state") }

    println(task.runToTry)
    println(task.runToEither)
    runToFuture(task)
    runOnComplete(task)
    runAsync(task)
    Thread.sleep(millisToSleep)
  }

  private def runToFuture[A](task: Task[A]): Unit =
    task.runToFuture.onComplete {
      case Failure(t) => println(t.toString)
      case Success(a) => println(a)
    }

  private def runOnComplete[A](task: Task[A]): Unit =
    task.runOnComplete { try_ =>
      println(try_.fold(_.toString, _.toString))
    }

  private def runAsync[A](task: Task[A]): Unit =
    task.runAsync { either =>
      println(either.fold(_.toString, _.toString))
    }

  println("-----\n")
}
