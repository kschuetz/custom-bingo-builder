package dev.marksman.custombingobuilder.service

import cats.data.Validated
import dev.marksman.custombingobuilder.types.SanitizedHtml
import org.owasp.html.{HtmlPolicyBuilder, PolicyFactory, Sanitizers}

trait TemplateSanitizer {
  def sanitizeTemplate(input: String): Validated[String, SanitizedHtml]
}

class DefaultTemplateSanitizer extends TemplateSanitizer {
  private val policy = buildOwaspPolicy

  def sanitizeTemplate(input: String): Validated[String, SanitizedHtml] =
    preValidate(input)
      .map(sanitizeWithOwasp)
      .map(postProcess)
      .map(SanitizedHtml)

  private def preValidate(input: String): Validated[String, String] = checkForDocType(input).map(_ => input)

  private def checkForDocType(input: String): Validated[String, Unit] =
    if (input.startsWith("<!DOCTYPE html>")) Validated.valid(())
    else Validated.invalid("template must start with <!DOCTYPE html>")

  private def sanitizeWithOwasp(input: String): String = policy.sanitize(input)

  private def postProcess(html: String): String = html

  private def buildOwaspPolicy: PolicyFactory = {
    val customPolicies = new HtmlPolicyBuilder()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowElements("html", "head", "title", "meta", "style", "body")
      .allowElements("span")
      .allowStyling()
      .allowTextIn("style", "span")
      .allowAttributes("class").onElements("div", "span", "table", "tr", "th", "td")
      .allowAttributes("type").onElements("style")
      .allowAttributes("media").onElements("style")
      .toFactory;

    customPolicies.and(Sanitizers.TABLES).and(Sanitizers.IMAGES)
  }
}


object DefaultTemplateSanitizer {
  def main(args: Array[String]): Unit = {
  }

  private def owaspTest(): Unit = {
    val customPolicies = new HtmlPolicyBuilder()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowElements("html", "head", "title", "meta", "style", "body")
      .allowElements("span")
      .allowStyling()
      .allowTextIn("style", "span")
      .allowAttributes("class").onElements("div", "span", "table", "tr", "th", "td")
      .allowAttributes("type").onElements("style")
      .allowAttributes("media").onElements("style")
      .toFactory;

    val policy = customPolicies.and(Sanitizers.TABLES).and(Sanitizers.IMAGES)

    println(policy.sanitize(template))
    /*
    PolicyFactory policy = new HtmlPolicyBuilder()
    .allowElements("p")
    .allowElements(
        (String elementName, List<String> attrs) -> {
          // Add a class attribute.
          attrs.add("class");
          attrs.add("header-" + elementName);
          // Return elementName to include, null to drop.
          return "div";
        }, "h1", "h2", "h3", "h4", "h5", "h6")
    .toFactory();
     */
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
              <table>
                <thead>
                  <tr>
                    <th colspan="5">BINGO</th>
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

