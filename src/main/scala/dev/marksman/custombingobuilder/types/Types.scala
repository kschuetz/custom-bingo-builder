package dev.marksman.custombingobuilder.types

object GroupId {
  def groupId(id: Int): GroupId = new GroupId(id)

  val default: GroupId = groupId(0)
}

class GroupId(val id: Int) extends AnyVal

case class Word[A](value: A,
                   group: GroupId = GroupId.default) {
  def map[B](f: A => B): Word[B] = Word(f(value), group)
}


case class SanitizedHtml(content: String)


case class CardData[A](words: Vector[Word[A]]) {
  def map[B](f: A => B): CardData[B] = CardData(words.map(_.map(f)))
}