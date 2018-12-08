package iomonad

object IOApp05 extends App {

  // IO[A] wraps a Function0[A].
  // With map, flatMap and pure it is a Monad usable in a for-comprehension
  //
  case class IO[A](run: () => A) {

    def map[B](f: A => B): IO[B] = IO { () => f(run()) }
    def flatMap[B](f: A => IO[B]): IO[B] = f(run())
  }

  object IO {
    def pure[A](value: A): IO[A] = IO { () => value }
    def eval[A](thunk: => A): IO[A] = IO { () => thunk }
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
  program.run()

  println("-----\n")
}
