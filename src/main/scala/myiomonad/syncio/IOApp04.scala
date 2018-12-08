package myiomonad.syncio

object IOApp04 extends App {

  // IO[A] wraps a Function0[A].
  // With map, flatMap and pure it is a Monad usable in a for-comprehension
  //
  case class IO[A](run: () => A) {

    def map[B](f: A => B): IO[B] = IO { () => f(run()) }

    def flatMap[B](f: A => IO[B]): IO[B] = f(run())
  }

  object IO {
    def pure[A](a: A): IO[A] = IO { () => a }
  }

  println("\n-----")

  // Program definition WITHOUT side effects. This program does nothing.
  // It is just a bunch of monadically composed functions which do not execute.
  //
  val program = for {
    welcome <- IO.pure("Welcome to Scala!")
    _       <- IO { () => print(s"$welcome  What's your name?   ") }
    name    <- IO { () => scala.io.StdIn.readLine }
    _       <- IO { () => println(s"Well hello, $name!") }
  } yield ()

  // Running the program's encapsulated Function0 produces the side effects.
  program.run()

  println("-----\n")
}
