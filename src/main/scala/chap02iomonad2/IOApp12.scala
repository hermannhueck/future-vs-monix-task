package chap02iomonad2

import chap02iomonad2.auth.{Password, User}
import chap02iomonad2.io.IO

import scala.concurrent.ExecutionContext
import scala.util.Try

object IOApp12 extends App {

  import Password._, User._

  /*
    // authenticate impl with flatMap
    def authenticate0(username: String, password: String): IO[Boolean] =
      IO(getUsers) map { users =>
        users.find(_.name == username)
      } flatMap { optUser =>
        IO(getPasswords) map { passwords =>
          optUser.isDefined && passwords.contains(Password(optUser.get.id, password))
        }
      }
  */

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


  def printTry[A](tryy: Try[A]): Unit = println(tryy.fold(_.toString, _.toString))

  def tryCallback[A]: Try[A] => Unit = printTry

  def printEither[A](either: Either[Throwable, A]): Unit = println(either.fold(_.toString, _.toString))

  def eitherCallback[A]: Either[Throwable, A] => Unit = printEither


  val checkMaggie: IO[Boolean] = authenticate("maggie", "maggie-pw")

  println("\n>>> IO#runToTry:")
  printTry(checkMaggie.runToTry)

  println("\n>>> IO#runToEither:")
  printEither(checkMaggie.runToEither)


  implicit val ec: ExecutionContext = ExecutionContext.global

  println("\n>>> IO#runToFuture:")
  checkMaggie.runToFuture onComplete tryCallback
  Thread sleep 500L

  println("\n>>> IO#runOnComplete:")
  checkMaggie runOnComplete tryCallback
  Thread sleep 500L

  println("\n>>> IO#runAsync:")
  checkMaggie runAsync eitherCallback
  Thread sleep 500L

  println("\n>>> IO.defer:")
  val io = IO.defer { IO.pure { println("side effect"); 5 } }
  Thread sleep 1000L
  val value = io.run()
  println(s"value = $value")
  Thread sleep 1000L

  println("-----\n")
}
