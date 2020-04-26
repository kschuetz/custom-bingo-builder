package dev.marksman.custombingobuilder.service

import java.util

import cats.data.Validated
import dev.marksman.custombingobuilder.types.{PopulatedCard, SanitizedHtml}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.util.control.NonFatal

object TemplateEngine {
  val CardTemplateClass = "card-template"

  val CardTemplateSelector = s"div.$CardTemplateClass"
  val InsertWordSelector = ".insert-word"

  val NotEnoughWords = "!!!not enough words provided!!!"
}

class TemplateEngine {

  import TemplateEngine._

  def render(templateSource: SanitizedHtml,
             cards: Vector[PopulatedCard[SanitizedHtml]]): Validated[String, String] =
    parseDocument(templateSource.content)
      .andThen(document =>
        findCardTemplate(document)
          .andThen(cardTemplate => findCardsHost(cardTemplate)
            .andThen(cardsHost => transform(document, cardTemplate, cardsHost, cards))))
      .map(renderDocument)

  private def parseDocument(source: String): Validated[String, Document] =
    try {
      Validated.valid(Jsoup.parse(source))
    } catch {
      case NonFatal(e) => Validated.invalid(s"Error parsing HTML: $e")
    }

  private def renderDocument(document: Document): String = {
    document.normalise();
    document.toString
  }

  private def findCardTemplate(document: Document): Validated[String, Element] =
    Validated.fromOption(Option(document.selectFirst(CardTemplateSelector)),
      "could not find card template")

  private def findCardsHost(cardTemplate: Element): Validated[String, Element] =
    Validated.fromOption(Option(cardTemplate.parent()),
      "card template has no parent")


  private def transform(document: Document, cardTemplate: Element, cardHost: Element,
                        cards: Vector[PopulatedCard[SanitizedHtml]]): Validated[String, Document] = {
    cardTemplate.remove();
    cardTemplate.removeClass(CardTemplateClass)
    val toInsert = new util.ArrayList[Element]
    cards.foreach(card => {
      val element = cardTemplate.clone()
      fillInCard(card, element)
      toInsert.add(element)
    })
    cardHost.insertChildren(0, toInsert)
    Validated.valid(document)
  }

  private def fillInCard(card: PopulatedCard[SanitizedHtml], element: Element): Unit = {
    val itemElements = element.select(InsertWordSelector)
    val size = itemElements.size()
    val wordCount = card.words.size
    for (i <- 0 until size) {
      val element = itemElements.get(i)
      if (i < wordCount) {
        element.html(card.words(i).content)
      } else {
        element.html(NotEnoughWords)
      }
    }
  }

}
