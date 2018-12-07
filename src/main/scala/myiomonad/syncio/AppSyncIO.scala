package myiomonad.syncio

object AppSyncIO extends App {

  println("\n-----")

  val program = for {
    _    <- SyncIO { () => print("Welcome to Scala!  What's your name?   ") }
    name <- SyncIO { () => scala.io.StdIn.readLine }
    _    <- SyncIO { () => println(s"Well hello, $name!") }
  } yield ()

  program.run()

  println("-----\n")
}