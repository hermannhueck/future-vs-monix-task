package iomonad

object IOApp02 extends App {

  println("\n-----")

  // Program definition WITHOUT side effects. This program does nothing.
  // It is just a Function0[Unit].
  //
  val program: () => Unit =      //  () => Unit  is syntactic sugar for:  Function0[Unit]
    () => {
      print("Welcome to Scala!  What's your name?   ")
      val name = scala.io.StdIn.readLine
      println(s"Well hello, $name!")
    }

  // Running the program produces the side effects.
  program()

  println("-----\n")
}
