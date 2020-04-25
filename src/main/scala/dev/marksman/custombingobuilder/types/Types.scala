package dev.marksman.custombingobuilder.types

case class SanitizedHtml(content: String)

case class PopulatedCard[A](words: Vector[A]) {
  def map[B](f: A => B): PopulatedCard[B] = PopulatedCard(words.map(f))
}