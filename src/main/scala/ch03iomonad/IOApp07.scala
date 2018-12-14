package ch03iomonad

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object IOApp07 extends App {

  // IO[A] wraps a Function0[A].
  // With map, flatMap and pure it is a Monad usable in a for-comprehension
  //
  case class IO[A](run: () => A) {

    def map[B](f: A => B): IO[B] = IO { () => f(run()) }
    def flatMap[B](f: A => IO[B]): IO[B] = f(run())

    // ----- different impure run* methods to be run at the end of the world

    // runs on the current Thread returning Try[A]
    def runToTry: Try[A] = Try { run() }

    // runs on the current Thread returning Either[Throwable, A]
    def runToEither: Either[Throwable, A] = runToTry.toEither

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
    def pure[A](a: A): IO[A] = IO { () => a }
    def eval[A](a: => A): IO[A] = IO { () => a }
  }

  println("\n-----")

  // Program definition WITHOUT side effects. This program does nothing.
  // It is just a bunch of monadically composed functions which do not execute.
  //
  val program: IO[Unit] = for {
    welcome <- IO.pure("Welcome to Scala!")
    _       <- IO.eval { print(s"$welcome  What's your name?   ") }
    name    <- IO.eval { scala.io.StdIn.readLine }
    _       <- IO.eval { println(s"Well hello, $name!") }
  } yield ()

  // Running the program's encapsulated Function0 produces the side effects.
  program.run()                                  // run sync, may throw an exception

  program.runToTry                               // run sync

  program.runToEither                            // run sync

  implicit val ec: ExecutionContext = ExecutionContext.global

  program.runToFuture onComplete {               // run async
    case Success(value) => println(value)
    case Failure(ex) => println(ex.toString)
  }

  program.runOnComplete {                        // run async
    case Success(value) => println(value)
    case Failure(ex) => println(ex.toString)
  }

  program.runAsync {                             // run async
    case Right(value) => println(value)
    case Left(ex) => println(ex.toString)
  }

  Thread.sleep(200L)
  println("-----\n")
}
