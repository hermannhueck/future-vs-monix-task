package chap02iomonad

/*
  In step 5 I added the IO companion object, which provides 'pure' and 'eval'.
  With 'pure' we can lift a pure value of type A into the IO context.
  'pure' is eager, i.e. executed immediately.
  'eval' takes a thunk (a block of computation) and turns it into a Function0[A] and wraps it into IO.
  'eval is lazy.

  This allows us to simplify the for-comprehension in our program a bit.
  We no longer need to specify functions in order to create instances of IO.
 */
object IOApp05PureAndEval extends App {

  case class IO[A](run: () => A) {

    import IO._

    def flatMap[B](f: A => IO[B]): IO[B] = IO { () => f(run()).run() }
    def map[B](f: A => B): IO[B] = flatMap(a => pure(f(a)))
    def flatten[B](implicit ev: A <:< IO[B]): IO[B] = flatMap(a => a)
  }

  object IO {
    def pure[A](value: A): IO[A] = IO { () => value }
    def eval[A](thunk: => A): IO[A] = IO { () => thunk }
  }

  println("\n-----")

  val program: IO[Unit] = for {
    welcome <- IO.pure("Welcome to Scala!")
    _       <- IO.eval { print(s"$welcome  What's your name?   ") }
    name    <- IO.eval { scala.io.StdIn.readLine }
    _       <- IO.eval { println(s"Well hello, $name!") }
  } yield ()

  // Running the program's encapsulated Function0 produces the side effects.
  program.run()
  program.run()

  println("-----\n")
}
