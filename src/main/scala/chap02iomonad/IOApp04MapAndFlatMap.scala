package chap02iomonad

/*
  In step 4 we add map and flatMap and flatten to the IO case class.
  This allows us to compose small IO instances to a bigger program.
  Composition can easily be done in a for-comprehension.
 */
object IOApp04MapAndFlatMap extends App {

  case class IO[A](run: () => A) {

    def flatMap[B](f: A => IO[B]): IO[B] = IO { () => f(run()).run() }
    def map[B](f: A => B): IO[B] = IO { () => f(run()) }
    def flatten[B](implicit ev: A <:< IO[B]): IO[B] = flatMap(a => a)
  }

  println("\n-----")

  // Program definition WITHOUT side effects. This program does nothing.
  // It is just a bunch of monadically composed functions which do not execute.
  //
  val program: IO[Unit] = for {
    _       <- IO { () => print(s"Welcome to Scala!  What's your name?   ") }
    name    <- IO { () => scala.io.StdIn.readLine }
    _       <- IO { () => println(s"Well hello, $name!") }
  } yield ()

  // Running the program's encapsulated Function0 produces the side effects.
  program.run()
  program.run()

  println("-----\n")
}
