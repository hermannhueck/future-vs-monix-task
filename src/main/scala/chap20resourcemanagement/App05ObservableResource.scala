package chap20resourcemanagement

import java.io.{BufferedReader, File, FileReader}

import cats.effect.Resource
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable

object App05ObservableResource extends App {

  def doCountLines(in: BufferedReader): Int = {
    var count: Int = 0
    var line: String = in.readLine()
    while (line != null) {
      count += 1
      line = in.readLine()
    }
    count
  }

  println(s"\n----- Main $currentThread")

  // unsused in this example
  def openFile(file: File): Resource[Task, BufferedReader] =
    Resource.fromAutoCloseable(
      Task(new BufferedReader(new FileReader(file)))
    )

  def openFileObs(file: File): Observable[BufferedReader] =
    Observable.resource(
      Task(new BufferedReader(new FileReader(file)))
    )( in =>
      Task(in.close())
    )

  def countLines(file: File): Observable[Int] =
    openFileObs(file).mapEval { in =>
      Task { doCountLines(in) }
    }

  val task: Task[Int] = countLines(new File("README.md")).firstL

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach { count =>
    println(s"File README.md has $count lines.")
  }

  Thread sleep 200L
  println("-----\n")
}
