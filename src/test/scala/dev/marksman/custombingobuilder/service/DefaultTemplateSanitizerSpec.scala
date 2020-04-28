package dev.marksman.custombingobuilder.service

import cats.data.Validated
import dev.marksman.custombingobuilder.types.SanitizedHtml
import org.scalatest.{FunSuite, Matchers}

class DefaultTemplateSanitizerSpec extends FunSuite with Matchers {

  private val sanitizer = new DefaultTemplateSanitizer

  test("requires <!DOCTYPE html> to be present") {
    sanitizer.sanitizeTemplate("<!DOCTYPE html>foobar") shouldBe Validated.valid(SanitizedHtml("foobar"))
    sanitizer.sanitizeTemplate("foobar").isInvalid shouldBe true
  }

  test("inline styling is OK") {
    sanitizer.sanitizeTemplate(createTemplate("<span style=\"color: white;width: 10px;\"></span>")) shouldBe Validated.valid(SanitizedHtml("<span style=\"color:white;width:10px\"></span>"))
  }

  private def createTemplate(template: String): String =
    "<!DOCTYPE html>" + template


}
