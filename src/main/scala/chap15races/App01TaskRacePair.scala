package chap15races

import monix.eval.{Fiber, Task}
import monix.execution.CancelableFuture
import monix.execution.Scheduler

import scala.concurrent.duration._
import scala.language.postfixOps

object App01TaskRacePair extends App {

  println(s"\n----- Main $currentThread")

  val random = scala.util.Random

  val task1 = Task(10 + 1).delayExecution(random.nextInt(3) seconds)
  val task2 = Task(20 + 1).delayExecution(random.nextInt(3) seconds)

  val raceTask: Task[Either[(Int, Fiber[Int]), (Fiber[Int], Int)]] =
    Task.racePair(task1, task2)

  implicit val scheduler: Scheduler = Scheduler.global

  val raceFuture: CancelableFuture[Either[(Int, Fiber[Int]), (Fiber[Int], Int)]] = raceTask.runToFuture

  raceFuture.foreach {
    case Left((result1, fiber)) =>
      fiber.cancel
      println(s"Task 1 succeeded with result: $result1")
    case Right((fiber, result2)) =>
      fiber.cancel
      println(s"Task 2 succeeded with result: $result2")
  }

  Thread sleep 3000L
  println("-----\n")
}
