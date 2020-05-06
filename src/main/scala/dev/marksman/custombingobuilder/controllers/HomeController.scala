package dev.marksman.custombingobuilder.controllers

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNec}
import cats.implicits._
import dev.marksman.custombingobuilder.Settings
import dev.marksman.custombingobuilder.service.{SheetGenerator, TemplateSanitizer, WordSanitizer}
import dev.marksman.custombingobuilder.types.{CardData, SanitizedHtml, Word}
import javax.inject._
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.{FileInfo, FilePartHandler}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               templateSanitizer: TemplateSanitizer,
                               wordSanitizer: WordSanitizer,
                               sheetGenerator: SheetGenerator,
                               settings: Settings) extends BaseController {
  private implicit val executionContext: ExecutionContext = controllerComponents.executionContext


  def index: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(settings))
  }

  def templateHelp: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.templateHelp(settings))
  }

  def generate: Action[MultipartFormData[ByteString]] = Action(parse.multipartFormData(handlePartAsFile,
    settings.maxTemplateSizeBytes)) { request =>

    validateForm(request.body) match {
      case Valid(fd) =>
        val sheet = sheetGenerator.generateSheet(fd.template, fd.wordList, fd.quantity)
        Ok(Html(sheet))
      case Invalid(e) =>
        val errors = e.toList.map(s => s"\t- $s\n").mkString("\n")
        BadRequest(s"Errors:\n$errors")
    }
  }

  private def handlePartAsFile: FilePartHandler[ByteString] = {
    case FileInfo(partName, filename, contentType, dispositionType) =>
      val sink = Sink.reduce[ByteString](_ ++ _)
      Accumulator(sink).map(content => FilePart(partName, filename, contentType, content, content.size, dispositionType))
  }

  private def prepareTemplate(formValue: Option[FilePart[ByteString]]): ValidatedNec[String, SanitizedHtml] = {
    formValue match {
      case None => Validated.invalidNec("Template not provided")
      case Some(FilePart(key, filename, contentType, data, fileSize, dispositionType)) =>
        val templateSource = data.utf8String
        if (templateSource.isEmpty) Validated.invalidNec("Template is blank")
        else templateSanitizer.sanitizeTemplate(templateSource)
    }
  }

  private def prepareWordList(formValue: String): ValidatedNec[String, CardData[SanitizedHtml]] = {
    val lines = formValue.split("\n")
    val sanitizedWords = lines.flatMap(wordSanitizer.sanitizeWord)
    Validated.validNec(CardData(sanitizedWords.map(sh => Word(sh)).toVector))
  }

  private def validateQuantity(formValue: String): ValidatedNec[String, Int] = {
    formValue.toIntOption match {
      case None => Validated.invalidNec("quantity must be a number")
      case Some(n) if n < 0 || n > settings.maxCardsPerSheet => Validated.invalidNec(s"quantity must be between 1 and ${settings.maxCardsPerSheet}")
      case Some(n) => Validated.validNec(n)
    }
  }

  private def requireExactlyOne(fieldName: String, data: Map[String, Seq[String]]): ValidatedNec[String, String] =
    data.get(fieldName).filter(_.nonEmpty).fold(Validated.invalidNec[String, String](s"$fieldName is required")) { ss: Seq[String] =>
      if (ss.size == 1) Validated.validNec(ss.head)
      else Validated.invalidNec(s"more than one value for $fieldName provided")
    }

  private def validateForm(mfd: MultipartFormData[ByteString]): ValidatedNec[String, FormData] = {
    (prepareTemplate(mfd.file("template")),
      requireExactlyOne("wordlist", mfd.dataParts).andThen(prepareWordList),
      requireExactlyOne("quantity", mfd.dataParts).andThen(validateQuantity)).mapN(FormData)
  }

  private case class FormData(template: SanitizedHtml,
                              wordList: CardData[SanitizedHtml],
                              quantity: Int)

}
