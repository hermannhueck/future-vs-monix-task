package chap20resourcemanagement

import java.io.{BufferedReader, File, FileReader}

import monix.eval.Task
import monix.execution.Scheduler
import monix.tail.Iterant

object App08Interant extends App {

  println(s"\n----- Main $currentThread")

  def openFile(file: File): Iterant[Task, BufferedReader] =
    Iterant[Task].resource(
      Task(new BufferedReader(new FileReader(file)))
    )(in =>
      Task(in.close())
    )

  def linesFromFile(file: File): Iterant[Task, String] =
    openFile(file).flatMap { in =>
      Iterant[Task]
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
