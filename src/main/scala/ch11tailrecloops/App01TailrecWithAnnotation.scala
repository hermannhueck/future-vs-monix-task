package ch11tailrecloops

object App01TailrecWithAnnotation extends App {

  println("\n-----")

  @scala.annotation.tailrec
  def fibonacci(cycles: Int, x: BigInt = 0, y: BigInt = 1): BigInt =
    if (cycles > 0)
      fibonacci(cycles-1, y, x + y)
    else
      y

  val result: BigInt = fibonacci(6)

  println(result)

  println("-----\n")
}
