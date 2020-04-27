package dev.marksman.custombingobuilder.service

import cats.data.Validated.{Invalid, Valid}
import dev.marksman.custombingobuilder.types.{CardData, SanitizedHtml}
import javax.inject.Inject

class SheetGenerator @Inject()(shufflerFactory: ShufflerFactory,
                               templateEngine: TemplateEngine) {
  private val random = new scala.util.Random

  def generateSheet(template: SanitizedHtml,
                    wordList: CardData[SanitizedHtml],
                    quantity: Int): String = {
    val seed = random.nextLong
    val shuffler = shufflerFactory.createShuffler(wordList.words, seed)
    val cards = (1 to quantity).foldLeft(Vector.empty[CardData[SanitizedHtml]]) {
      case (acc, _) => acc :+ CardData(shuffler.nextShuffle)
    }
    templateEngine.render(template, cards) match {
      case Valid(result) => result
      case Invalid(errors) =>
        s"TEMPLATE ERROR: $errors"
    }

    // TODO: separate template validation from render

  }

}
