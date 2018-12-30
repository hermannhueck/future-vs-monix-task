package chap14errorhandling

import monix.eval.Task
import monix.execution.Scheduler

import scala.util.Random

object App11TaskRestartUntil extends App {

  println(s"\n----- Main $currentThread")

  val random = Task.eval(Random.nextInt())

  val predicate: Int => Boolean = _ % 2 == 0
  val randomEven = random.restartUntil(predicate)

  implicit val scheduler: Scheduler = Scheduler.global

  randomEven.runToFuture foreach println
  //=> -2097793116
  randomEven.runToFuture foreach println
  //=> 1246761488
  randomEven.runToFuture foreach println
  //=> 1053678416

  Thread sleep 1000L
  println("-----\n")
}
