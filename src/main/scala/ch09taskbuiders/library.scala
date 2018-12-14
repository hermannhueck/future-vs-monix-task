package ch09taskbuiders

import scala.concurrent.{ExecutionContext, Future}

object library {

  def futureFactorial(n: Int)(implicit ec: ExecutionContext): Future[BigInt] = Future {
    println(s"side effect on $currentThread")
    factorial(n)
  }
}
