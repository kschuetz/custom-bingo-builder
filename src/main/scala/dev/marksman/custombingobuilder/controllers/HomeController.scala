package dev.marksman.custombingobuilder.controllers

import akka.stream.scaladsl.Sink
import akka.util.ByteString
import cats.data.Validated.{Invalid, Valid}
import dev.marksman.custombingobuilder.service.TemplateSanitizer
import javax.inject._
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.api.mvc._
import play.core.parsers.Multipart.{FileInfo, FilePartHandler}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               templateSanitizer: TemplateSanitizer) extends BaseController {
  private implicit val executionContext = controllerComponents.executionContext

  private val MaxTemplateSize = 32768

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }


  def run = Action(parse.multipartFormData(handlePartAsFile,
    MaxTemplateSize)) { request =>
    val template = request.body.file("template").map {
      case FilePart(key, filename, contentType, data, fileSize, dispositionType) =>
        data.utf8String
    }

    templateSanitizer.sanitizeTemplate(template.getOrElse("")) match {
      case Valid(sanitizedTemplate) => Ok(s"template: ${sanitizedTemplate.content}")
      case Invalid(e) => BadRequest(e.toString)
    }
  }

  private def handlePartAsFile: FilePartHandler[ByteString] = {
    case FileInfo(partName, filename, contentType, dispositionType) =>
      val sink = Sink.reduce[ByteString](_ ++ _)
      Accumulator(sink).map(content => FilePart(partName, filename, contentType, content, content.size, dispositionType))
  }

}
