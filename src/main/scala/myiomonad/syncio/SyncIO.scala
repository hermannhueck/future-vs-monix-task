package myiomonad.syncio

case class SyncIO[A](run: () => A) {

  def map[B](f: A => B): SyncIO[B] = SyncIO { () => f(run()) }

  def flatMap[B](f: A => SyncIO[B]): SyncIO[B] = f(run())
}
