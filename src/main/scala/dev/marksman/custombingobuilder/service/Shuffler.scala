package dev.marksman.custombingobuilder.service

trait Shuffler[A] {
  def nextShuffle: Vector[A]
}

trait ShufflerFactory {
  def createShuffler[A](candidates: Vector[A], seed: Long): Shuffler[A]
}

class DefaultShuffler[A](candidates: Vector[A],
                         seed: Long) extends Shuffler[A] {
  private val random = new scala.util.Random(seed)

  def nextShuffle: Vector[A] = random.shuffle(candidates)
}

object DefaultShufflerFactory extends ShufflerFactory {
  def createShuffler[A](candidates: Vector[A], seed: Long): Shuffler[A] = new DefaultShuffler[A](candidates, seed)
}
