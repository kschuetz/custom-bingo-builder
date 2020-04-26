package dev.marksman.custombingobuilder.service

import dev.marksman.kraftwerk.{Generators, Seed, ValueSupply}
import zio.{Task, UIO}

import scala.util.Random

trait Shuffler[A] {
  def nextShuffle: UIO[Vector[A]]
}

class DefaultShuffler[A](candidates: Vector[A],
                         seed: Long) extends Shuffler[A] {
  private val supply = buildSupply(candidates, seed)
  private val iterator = supply.iterator()

  def nextShuffle: UIO[Vector[A]] = UIO(iterator.next)

  private def buildSupply(candidates: Vector[A],
                          seed: Long): ValueSupply[Vector[A]] = {
    import scala.jdk.CollectionConverters._
    type JVector = dev.marksman.collectionviews.Vector[A]
    val input = new JVector {

      override def size(): Int = candidates.size

      def unsafeGet(index: Int): A = candidates(index)
    }
    Generators.generateShuffled(input).fmap(xs => Vector() ++ xs.asScala)
      .run(Seed.create(seed))
  }

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

    (1 to 20).foreach(_ => runtime.unsafeRun(test))

  }

}