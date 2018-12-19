import scala.util.Try

package object ch02iomonad extends tools.Tools {


  def printAuthTry[A](tryy: Try[A]): Unit = println(
    tryy.fold(
      ex => ex.toString,
      isAuthenticated => s"isAuthenticated = $isAuthenticated")
  )

  def printAuthEither[A](either: Either[Throwable, A]): Unit =
    println(either.fold(
      ex => ex.toString,
      isAuthenticated => s"isAuthenticated = $isAuthenticated")
    )


  def authCallbackTry[A]: Try[A] => Unit = printAuthTry

  def authCallbackEither[A]: Either[Throwable, A] => Unit = printAuthEither

}
