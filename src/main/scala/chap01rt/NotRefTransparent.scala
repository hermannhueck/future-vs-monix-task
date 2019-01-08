package chap01rt

/*
  see: https://typelevel.org/blog/2017/05/02/io-monad-for-cats.html
 */
object NotRefTransparent extends App {

  println("\n-----")

  def func(ioa1: Unit, ioa2: Unit): Unit = {
    ioa1
    ioa2
  }

  func(println("hi"), println("hi"))        // prints "hi" twice
  //=> hi
  //=> hi

  println("-----")

  val x: Unit = println("hi")
  func(x, x)                                // prints "hi" once
  //=> hi

  // 'func' IS NOT referentially transparent!

  println("-----\n")
}
