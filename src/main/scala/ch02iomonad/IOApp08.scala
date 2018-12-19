package ch02iomonad

import ch02iomonad.auth._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/*
  trait IO instead of case class IO
  IO is an ADT
  abstract run method
 */
object IOApp08 extends App {

  trait IO[A] {

    def run: () => A

    def map[B](f: A => B): IO[B] = IO { f(run()) }
    def flatMap[B](f: A => IO[B]): IO[B] = IO { f(run()).run() }

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

    private case class Pure[A](run: () => A) extends IO[A]
    private case class Eval[A](run: () => A) extends IO[A]

    def pure[A](a: A): IO[A] = Pure { () => a }

    def eval[A](a: => A): IO[A] = Eval { () => a }
    def delay[A](a: => A): IO[A] = eval(a)
    def apply[A](a: => A): IO[A] = eval(a)
  }



  import Password._, User._

  // authenticate impl with for-comprehension
  def authenticate(username: String, password: String): IO[Boolean] =
    for {
      optUser <- IO(getUsers) map { users =>
        users.find(_.name == username)
      }
      authenticated <- IO(getPasswords) map { passwords =>
        optUser.isDefined && passwords.contains(Password(optUser.get.id, password))
      }
    } yield authenticated



  println("\n-----")

  IO(getUsers).run() foreach println
  println("-----")

  IO(getPasswords).run() foreach println
  println("-----")

  println("\n>>> IO#run: authenticate:")
  println(authenticate("maggie", "maggie-pw").run())
  println(authenticate("maggieXXX", "maggie-pw").run())
  println(authenticate("maggie", "maggie-pwXXX").run())


  val checkMaggie: IO[Boolean] = authenticate("maggie", "maggie-pw")

  println("\n>>> IO#runToTry:")
  printAuthTry(checkMaggie.runToTry)

  println("\n>>> IO#runToEither:")
  printAuthEither(checkMaggie.runToEither)

  implicit val ec: ExecutionContext = ExecutionContext.global

  println("\n>>> IO#runToFuture:")
  checkMaggie.runToFuture onComplete authCallbackTry
  Thread sleep 500L

  println("\n>>> IO#runOnComplete:")
  checkMaggie runOnComplete authCallbackTry
  Thread sleep 500L

  println("\n>>> IO#runAsync:")
  checkMaggie runAsync authCallbackEither
  Thread sleep 500L

  println("-----\n")
}
