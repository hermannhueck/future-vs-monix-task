package chap02iomonad

/*
  An IO program with side effects is IMPURE.
 */
object IOApp01 extends App {

  println("\n-----")

  // program definition WITH side effects
  def program(): Unit = {
    print("Welcome to Scala!  What's your name?   ")
    val name = scala.io.StdIn.readLine
    println(s"Well hello, $name!")
  }

  program()
  program()

  println("-----\n")
}
