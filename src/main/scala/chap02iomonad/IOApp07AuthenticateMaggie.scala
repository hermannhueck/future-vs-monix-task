package chap02iomonad

import chap02iomonad.auth._

import scala.util.Try

/*
  Before adding asynchronous run* methods in step 8 I use another example program.
  The previous interactive program is not very suitable to demonstrate asynchrony.

  The 'authenticate' method accesses the files 'users.txt' and 'passwords.txt'
  to check a username and a password and returns true if the specified username
  exists in 'users.txt' and the specified password matches with the user's passord in 'passwords.txt'.
 */
object IOApp07AuthenticateMaggie extends App {

  // IO[A] wraps a Function0[A].
  // With map, flatMap and pure it is a Monad usable in a for-comprehension
  //
  case class IO[A](run: () => A) {

    def map[B](f: A => B): IO[B] = IO { () => f(run()) }
    def flatMap[B](f: A => IO[B]): IO[B] = IO { () => f(run()).run() }

    // ----- impure sync run* methods

    // runs on the current Thread returning Try[A]
    def runToTry: Try[A] = Try { run() }

    // runs on the current Thread returning Either[Throwable, A]
    def runToEither: Either[Throwable, A] = runToTry.toEither
  }

  object IO {
    def pure[A](a: A): IO[A] = IO { () => a }
    def eval[A](a: => A): IO[A] = IO { () => a }
  }



  import Password._
  import User._

  def authenticate(username: String, password: String): IO[Boolean] =
    for {
      optUser <- IO.eval(getUsers) map { users =>
        users.find(_.name == username)
      }
      authenticated <- IO.eval(getPasswords) map { passwords =>
        optUser.isDefined && passwords.contains(Password(optUser.get.id, password))
      }
    } yield authenticated



  println("\n-----")

  IO.eval(getUsers).run() foreach println
  println("-----")

  IO.eval(getPasswords).run() foreach println
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

  println("-----\n")
}
