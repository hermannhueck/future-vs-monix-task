package chap02iomonad

import chap02iomonad.auth._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/*
  Step 17 provides IO.deferFutureAction which allows to provide the EC when the IO is run.
  - I expanded the ADT IO again with a new subtype FutureToTask which wraps a function of type: ExecutionContext => Future[A]
  - IO.deferFutureAction just creates an instance of FutureToTask passing the function it received to it.
  - The IO#run method in trait IO has a new case for FutureToTask which applies the wrapped function to the implicitly passed EC,
    turns it into an IO with fromFuture and runs it passing the ec again.
 */
object IOApp17 extends App {

  trait IO[A] {

    import IO._

    private def run(implicit ec: ExecutionContext): A = this match {
      case Pure(thunk) => thunk()
      case Eval(thunk) => thunk()
      case Suspend(thunk) => thunk().run
      case FlatMap(src, f) => f(src.run).run
      case FutureToTask(fEcToFuture) => fromFuture(fEcToFuture(ec)).run(ec)
    }

    def map[B](f: A => B): IO[B] = flatMap(a => pure(f(a)))
    def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)

    // ----- impure sync run* methods

    // runs on the current Thread returning Try[A]
    def runToTry(implicit ec: ExecutionContext): Try[A] = Try { run }

    // runs on the current Thread returning Either[Throwable, A]
    def runToEither(implicit ec: ExecutionContext): Either[Throwable, A] = runToTry.toEither

    // ----- impure async run* methods

    // returns a Future that runs the task eagerly on another thread
    def runToFuture(implicit ec: ExecutionContext): Future[A] = Future { run }

    // runs the IO in a Runnable on the given ExecutionContext
    // and then executes the specified Try based callback
    def runOnComplete(callback: Try[A] => Unit)(implicit ec: ExecutionContext): Unit = {
      // convert Try based callback into an Either based callback
      val eitherCallback: Either[Throwable, A] => Unit = (ea: Either[Throwable, A]) => callback(ea.toTry)
      runAsync0(ec, eitherCallback)
    }

    // runs the IO in a Runnable on the given ExecutionContext
    // and then executes the specified Either based callback
    def runAsync(callback: Either[Throwable, A] => Unit)(implicit ec: ExecutionContext): Unit = {
      runAsync0(ec, callback)
    }

    private val runAsync0: (ExecutionContext, Either[Throwable, A] => Unit) => Unit = {
      (ec: ExecutionContext, callback: Either[Throwable, A] => Unit) =>
        ec.execute(new Runnable {
          override def run(): Unit = callback(runToEither(ec))
        })
    }

    // Triggers async evaluation of this IO, executing the given function for the generated result.
    // WARNING: Will not be called if this IO is never completed or if it is completed with a failure.
    // Since this method executes asynchronously and does not produce a return value,
    // any non-fatal exceptions thrown will be reported to the ExecutionContext.
    def foreach(f: A => Unit)(implicit ec: ExecutionContext): Unit =
      runAsync {
        case Left(ex) => ec.reportFailure(ex)
        case Right(value) => f(value)
      }
  }

  object IO {

    private case class Pure[A](thunk: () => A) extends IO[A]
    private case class Eval[A](thunk: () => A) extends IO[A]
    private case class Suspend[A](thunk: () => IO[A]) extends IO[A]
    private case class FlatMap[A, B](src: IO[A], f: A => IO[B]) extends IO[B]
    private case class FutureToTask[A](f: ExecutionContext => Future[A]) extends IO[A]

    def pure[A](a: A): IO[A] = Pure { () => a }

    def eval[A](a: => A): IO[A] = Eval { () => a }
    def delay[A](a: => A): IO[A] = eval(a)
    def apply[A](a: => A): IO[A] = eval(a)

    def suspend[A](ioa: => IO[A]): IO[A] = Suspend(() => ioa)
    def defer[A](ioa: => IO[A]): IO[A] = suspend(ioa)

    def fromTry[A](tryy: Try[A]): IO[A] = IO {
      tryy match {
        case Failure(t) => throw t
        case Success(value) => value
      }
    }

    def fromEither[A](either: Either[Throwable, A]): IO[A] = IO {
      either match {
        case Left(t) => throw t
        case Right(value) => value
      }
    }

    def fromFuture[A](fa: Future[A]): IO[A] =
      fa.value match {
        case Some(try0) => fromTry(try0)
        case None => IO.eval { Await.result(fa, Duration.Inf) } // eval is lazy!
      }

    def deferFuture[A](fa: => Future[A]): IO[A] =
      defer(IO.fromFuture(fa))

    def deferFutureAction0[A](f: ExecutionContext => Future[A]): IO[A] = {
      def runIt(f0: ExecutionContext => Future[A])(implicit ec: ExecutionContext): IO[A] = deferFuture(f0(ec))
      implicit lazy val ec0: ExecutionContext = ExecutionContext.global
      runIt(f)
    }

    def deferFutureAction[A](fEcToFuture: ExecutionContext => Future[A]): IO[A] =
      FutureToTask(fEcToFuture)
  }



  def futureGetUsers(implicit ec: ExecutionContext): Future[Seq[User]] = {
    Future {
      println("===> side effect <===")
      User.getUsers
    }
  }

  {
    // EC needed to turn a Future into an IO
    implicit val ec: ExecutionContext = ExecutionContext.global

    println("\n>>> IO.deferFuture(future)")
    println("----- side effect performed lazily")
    val io = IO.deferFuture { futureGetUsers }

    io foreach { users => users foreach println } // prints "side effect"
    io foreach { users => users foreach println } // prints "side effect"
    Thread sleep 1000L
  }

  {
    println("\n>>> IO.deferFutureAction(implicit ec => future)")
    println("----- side effect performed lazily")
    val io = IO.deferFutureAction { implicit ec: ExecutionContext => futureGetUsers }

    // EC needed to run the IO
    implicit val ec: ExecutionContext = ExecutionContext.global

    io foreach { users => users foreach println } // prints "side effect"
    io foreach { users => users foreach println } // prints "side effect"
    Thread sleep 1000L
  }

  println("-----\n")
}
