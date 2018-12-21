package chap12asynboundary

import monix.eval.Task
import monix.execution.Scheduler

object App06AsynchronyOnOneThread extends App {

  def someComputation(id: Int): Int = {
    println(s"Computation no $id running on $currentThread")
    fibonacci(300000)
    id
  }

  println(s"\n----- Main $currentThread")

  def createTask(id: Int): Task[Int] =
  Task { println(s"Task no $id starting on $currentThread"); id }
    .asyncBoundary
    .map { someComputation }
    .asyncBoundary
    .map { someComputation }
    .asyncBoundary
    .map { someComputation }
    .asyncBoundary
    .map { someComputation }

  implicit val scheduler: Scheduler = Scheduler.singleThread("SingleThreadScheduler")

  createTask(1) runAsync (_ => println(s"Task no 1 finished on $currentThread"))
  createTask(2) runAsync (_ => println(s"Task no 2 finished on $currentThread"))

  Thread sleep 12000L
  println("-----\n")
}
