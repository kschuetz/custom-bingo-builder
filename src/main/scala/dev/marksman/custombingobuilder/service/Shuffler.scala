package dev.marksman.custombingobuilder.service

trait Shuffler[A] {
  def nextShuffle: Vector[A]
}

class DefaultShuffler[A](candidates: Vector[A],
                         seed: Long) extends Shuffler[A] {
  private val random = new scala.util.Random(seed)

  def nextShuffle: Vector[A] = random.shuffle(candidates)
}
