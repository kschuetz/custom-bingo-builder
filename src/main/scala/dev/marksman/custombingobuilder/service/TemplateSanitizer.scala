package dev.marksman.custombingobuilder.service

import cats.data.{Validated, ValidatedNec}
import dev.marksman.custombingobuilder.types.SanitizedHtml
import org.owasp.html.{HtmlPolicyBuilder, PolicyFactory, Sanitizers}

trait TemplateSanitizer {
  def sanitizeTemplate(input: String): ValidatedNec[String, SanitizedHtml]
}

class DefaultTemplateSanitizer extends TemplateSanitizer {
  private val policy = buildOwaspPolicy

  def sanitizeTemplate(input: String): ValidatedNec[String, SanitizedHtml] =
    preValidate(input)
      .map(sanitizeWithOwasp)
      .map(SanitizedHtml)

  private def preValidate(input: String): ValidatedNec[String, String] = checkForDocType(input).map(_ => input)

  private def checkForDocType(input: String): ValidatedNec[String, Unit] =
    if (input.startsWith("<!DOCTYPE html>")) Validated.validNec(())
    else Validated.invalidNec("template must start with <!DOCTYPE html>")

  private def sanitizeWithOwasp(input: String): String = policy.sanitize(input)

  private def buildOwaspPolicy: PolicyFactory = {
    val customPolicies = new HtmlPolicyBuilder()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowElements("html", "head", "title", "meta", "style", "body")
      .allowElements("span")
      .allowElements("th", "td")
      .allowStyling()
      .allowTextIn("style", "span")
      .allowAttributes("class").globally()
      .allowAttributes("colspan").onElements("th", "td")
      .allowAttributes("rowspan").onElements("th", "td")
      .allowAttributes("type").onElements("style")
      .allowAttributes("media").onElements("style")
      .allowAttributes("lang").onElements("html")
      .allowAttributes("charset", "name", "content").onElements("meta")
      .toFactory

    customPolicies.and(Sanitizers.TABLES).and(Sanitizers.IMAGES)
  }
}


object DefaultTemplateSanitizer {
  def main(args: Array[String]): Unit = {
    owaspTest()
  }

  private def owaspTest(): Unit = {
    val customPolicies = new HtmlPolicyBuilder()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowElements("html", "head", "title", "meta", "style", "body")
      .allowElements("span")
      .allowElements("th", "td")
      .allowStyling()
      .allowTextIn("style", "span")
      .allowAttributes("class").globally()
      .allowAttributes("colspan").onElements("th", "td")
      .allowAttributes("rowspan").onElements("th", "td")
      .allowAttributes("type").onElements("style")
      .allowAttributes("media").onElements("style")
      .allowAttributes("lang").onElements("html")
      .allowAttributes("charset", "name", "content").onElements("meta")
      .toFactory

    val policy = Sanitizers.TABLES.and(Sanitizers.IMAGES).and(customPolicies)

    println(policy.sanitize(template))
  }


  private val template =
    """
    <!DOCTYPE html>
      <!-- saved from url=(0041)http://localhost:9000/templates/simple5x5 -->
        <html lang="en"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

          <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>Simple 5x5 Template</title>
            <style type="text/css">

              table {
              background-color: black;
              border-collapse: collapse;
              margin-bottom: 1em;
              margin-right: 1em;
              float: left;
              }

              td, th {
              border: 1px solid black;
              text-align: center;
              }

              th {
              color: white;
              height: 60px;
              font-size: 32px;
              }

              td {
              background-color: white;
              width: 100px;
              height: 100px;
              }

            </style>
            <style type="text/css" media="print">

            </style>
          </head><body>

          <a href="/foobar!"/>
          <img src="//example.com/">
          <img src="/relative">
          <img src=javascript:alert(1337)>

            <!-- div with class "card-template" will be filled in and repeated -->
            <!-- elements with class "insert-word" will be filled in with items from word list -->
            <!-- (the existing content, if any, will be replaced) -->
            <div class="card-template">
              <table class="foo">
                <thead>
                  <tr>
                    <th colspan=5>BINGO</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                  </tr>
                  <tr>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                  </tr>
                  <tr>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><strong>Free Space</strong></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                  </tr>
                  <tr>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                  </tr>
                  <tr>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                    <td><span class="insert-word"></span></td>
                  </tr>
                </tbody>
              </table>
            </div>


          </body></html>
          """
}

