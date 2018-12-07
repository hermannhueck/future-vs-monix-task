package myiomonad.asyncio.task

import java.util.concurrent.TimeUnit

import monix.execution.Scheduler

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Task {

  type Callback[A] = Either[Throwable, A] => Unit

  def now[A](a: A): Task[A] = Task { () => a }

  def pure[A](a: A): Task[A] = now(a)

  def raiseError[A](t: Throwable): Task[A] = Task[A] { () => throw t }

  def eval[A](a: => A): Task[A] = suspend(a)

  def suspend[A](a: => A): Task[A] = Task { () => a }
}

case class Task[A](run: () => A) { self =>

  import Task._

  def map[B](f: A => B): Task[B] = Task { () => f(run()) }

  def flatMap[B](f: A => Task[B]): Task[B] = f(run())


  // ----- different impure run* methods to be run at the end of the world

  // runs on the current Thread returning Try[A]
  def runToTry: Try[A] = Try { run() }

  // runs on the current Thread returning Either[Throwable, A]
  def runToEither: Either[Throwable, A] = runToTry.toEither

  // returns a Future that runs the task eagerly on another thread
  def runToFuture(implicit ec: ExecutionContext): Future[A] = Future { run() }

  // runs the task on the given ExecutionContext
  // and then executes the specified Try based callback
  def runOnComplete(callback: Try[A] => Unit)(implicit ec: ExecutionContext): Unit = {
    ec.execute(() => callback(runToTry))
  }

  // runs the task on the given Scheduler
  // and then executes the specified Either based callback
  def runAsync(callback: Either[Throwable, A] => Unit)(implicit scheduler: Scheduler): Unit = {
    scheduler.scheduleOnce(0L, TimeUnit.SECONDS, () => callback(runToEither))
  }
}
