package chap20resourcemanagement

import java.io.{BufferedReader, File, FileReader}

import cats.effect.Resource
import monix.eval.Task
import monix.execution.Scheduler

object App04ResourceFromAutoCloseable extends App {

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

  def openFile(file: File): Resource[Task, BufferedReader] =
    Resource.fromAutoCloseable(
      Task(new BufferedReader(new FileReader(file)))
    )

  def countLines(file: File): Task[Int] =
    openFile(file).use { in =>
      Task { doCountLines(in) }
    }

  val task: Task[Int] = countLines(new File("README.md"))

  implicit val scheduler: Scheduler = Scheduler.global

  task foreach { count =>
    println(s"File README.md has $count lines.")
  }

  Thread sleep 200L
  println("-----\n")
}
