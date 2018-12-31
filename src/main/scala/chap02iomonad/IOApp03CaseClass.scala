package chap02iomonad

/*
  In step 3 the Function0[A] is named 'run' and wrapped in a case class.
  To run the program we must unwrap the 'run' function and invoke it.
 */
object IOApp03CaseClass extends App {

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
