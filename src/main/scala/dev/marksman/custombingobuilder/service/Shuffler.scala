package dev.marksman.custombingobuilder.service

import zio.{Task, UIO}

import scala.util.Random

trait Shuffler[A] {
  def nextShuffle: UIO[Vector[A]]
}

class DefaultShuffler[A](candidates: Vector[A],
                         seed: Long) extends Shuffler[A] {
  private val random = new scala.util.Random(seed)

  def nextShuffle: UIO[Vector[A]] = UIO(random.shuffle(candidates))
}

object DefaultShuffler {
  def main(args: Array[String]): Unit = {
    import zio.Runtime

    val runtime = Runtime.default

    val shuffler = new DefaultShuffler[Int](Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), Random.nextLong())

    def test: Task[Unit] = for {
      xs <- shuffler.nextShuffle
      _ <- Task(println(xs))
    } yield ()

    (1 to 100).foreach(_ => runtime.unsafeRun(test))

  }

}