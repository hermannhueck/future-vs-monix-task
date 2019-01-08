package chap01rt

import chap02iomonad.IOApp05PureAndEval.IO

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object RefTransparent extends App {

  println("\n-----")

  def putStrLn(line: String): IO[Unit] =
    IO.eval { println(line) }

  def func(ioa1: IO[Unit], ioa2: IO[Unit]): IO[Unit] = for {
    _ <- ioa1
    _ <- ioa2
  } yield ()

  func(putStrLn("hi"), putStrLn("hi")).run()   // prints "hi" twice
  //=> hi
  //=> hi

  println("-----")

  val x: IO[Unit] = putStrLn("hi")
  func(x, x).run()                                         // prints "hi" twice
  //=> hi
  //=> hi

  // 'func' IS referentially transparent!

  println("-----\n")
}
