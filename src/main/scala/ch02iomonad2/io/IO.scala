package ch02iomonad2.io

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait IO[A] {

  import IO._

  def run(): A = this match {
    case Pure(thunk) => thunk()
    case Eval(thunk) => thunk()
    case Suspend(thunk) => thunk().run()
    case FlatMap(src, f) => f(src.run()).run()
  }

  def map[B](f: A => B): IO[B] = flatMap(a => pure(f(a)))

  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

  def filter(p: A => Boolean): A = {
    val value = run()
    if (p(value)) value else throw new NoSuchElementException
  }

  def withFilter(p: A => Boolean): A = filter(p)

  // ----- impure sync run* methods

  // runs on the current Thread returning Try[A]
  def runToTry: Try[A] = Try { run() }

  // runs on the current Thread returning Either[Throwable, A]
  def runToEither: Either[Throwable, A] = runToTry.toEither

  // ----- impure async run* methods

  // returns a Future that runs the task eagerly on another thread
  def runToFuture(implicit ec: ExecutionContext): Future[A] = Future { run() }

  // runs the IO in a Runnable on the given ExecutionContext
  // and then executes the specified Try based callback
  def runOnComplete(callback: Try[A] => Unit)(implicit ec: ExecutionContext): Unit = {
    ec.execute(new Runnable {
      override def run(): Unit = callback(runToTry)
    })
  }

  // runs the IO in a Runnable on the given ExecutionContext
  // and then executes the specified Either based callback
  def runAsync(callback: Either[Throwable, A] => Unit)(implicit ec: ExecutionContext): Unit = {
    ec.execute(new Runnable {
      override def run(): Unit = callback(runToEither)
    })
  }
}

object IO {

  private case class Pure[A](thunk: () => A) extends IO[A]
  private case class Eval[A](thunk: () => A) extends IO[A]
  private case class Suspend[A](thunk: () => IO[A]) extends IO[A]
  private case class FlatMap[A, B](src: IO[A], f: A => IO[B]) extends IO[B]

  def pure[A](a: A): IO[A] = Pure { () => a }
  def now[A](a: A): IO[A] = pure(a)

  def eval[A](a: => A): IO[A] = Eval { () => a }
  def delay[A](a: => A): IO[A] = eval(a)
  def apply[A](a: => A): IO[A] = eval(a)

  def suspend[A](ioa: => IO[A]): IO[A] = Suspend(() => ioa)
  def defer[A](ioa: => IO[A]): IO[A] = suspend(ioa)
}
