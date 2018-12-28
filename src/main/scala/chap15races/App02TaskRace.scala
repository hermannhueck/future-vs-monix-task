package chap15races

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.language.postfixOps

object App02TaskRace extends App {

  println(s"\n----- Main $currentThread")

  val random = scala.util.Random

  val task1 = Task(10 + 1).delayExecution(random.nextInt(3) seconds)
  val task2 = Task(20 + 1).delayExecution(random.nextInt(3) seconds)

  val winnerTask: Task[Either[Int, Int]] = Task.race(task1, task2)

  implicit val scheduler: Scheduler = Scheduler.global

  winnerTask
    .runToFuture
    .foreach(result => println(s"Winner's result: $result"))

  Thread sleep 3000L
  println("-----\n")
}
