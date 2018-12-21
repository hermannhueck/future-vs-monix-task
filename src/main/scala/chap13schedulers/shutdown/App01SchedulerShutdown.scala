package chap13schedulers.shutdown

import monix.execution.Scheduler
import monix.execution.Scheduler.global
import monix.execution.schedulers.SchedulerService

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object App01SchedulerShutdown extends App {

  println(s"\n----- Main $currentThread")

  val io: SchedulerService = Scheduler.io("my-io")

  io.execute(() => {
    Thread sleep 5000L
    println(s"Running on $currentThread")
    println("Hello, world!")
  })

  io.shutdown()

  println(s"isShutdown = ${io.isShutdown}")

  val termination: Future[Boolean] = io.awaitTermination(10.seconds, global)
  Await.ready(termination, Duration.Inf)

  println(s"isTerminated = ${io.isTerminated}")

  println("-----\n")
}
