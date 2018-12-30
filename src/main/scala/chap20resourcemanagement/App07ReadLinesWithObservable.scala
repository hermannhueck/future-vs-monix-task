package chap20resourcemanagement

import java.io.{BufferedReader, File, FileReader}

import cats.effect.Resource
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

object App07ReadLinesWithObservable extends App {

  println(s"\n----- Main $currentThread")

  def openFile(file: File): Resource[Task, BufferedReader] =
    Resource.fromAutoCloseable(
      Task(new BufferedReader(new FileReader(file)))
    )

  def linesFromFile(file: File): Observable[String] =
    Observable.fromResource(openFile(file)).flatMap { in =>
      Observable
        .repeatEval(in.readLine())
        .takeWhile(line => line != null)
    }

  def countLines(file: File): Task[Long] =
    linesFromFile(file).countL

  val task: Task[Long] = countLines(new File("README.md"))

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach { count =>
    println(s"File README.md has $count lines.")
  }

  Thread sleep 200L
  println("-----\n")
}
