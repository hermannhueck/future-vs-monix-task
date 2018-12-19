package ch02iomonad

object IOApp03 extends App {

  // IO[A] wraps a Function0[A]
  //
  case class IO[A](run: () => A)

  println("\n-----")

  // Program definition WITHOUT side effects. This program does nothing.
  // It is just a Function0[Unit] wrapped in IO.
  //
  val program: IO[Unit] = IO {
    () => {
      print("Welcome to Scala!  What's your name?   ")
      val name = scala.io.StdIn.readLine
      println(s"Well hello, $name!")
    }
  }

  // Running the program's encapsulated Function0 produces the side effects.
  program.run()
  program.run()

  println("-----\n")
}
