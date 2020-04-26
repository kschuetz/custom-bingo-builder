package dev.marksman.custombingobuilder.service

import dev.marksman.custombingobuilder.types.{CardData, SanitizedHtml, Word}
import org.scalatest.{FunSuite, Matchers}

class TemplateEngineSpec extends FunSuite with Matchers {

  private val justRight = buildCardData(Vector("foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply"))
  private val tooMany = buildCardData(Vector("foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply", "too-many"))
  private val notEnough = buildCardData(Vector("foo", "bar", "baz"))

  private val htmlItems = buildCardData(Vector("<b>simple</b>",
    "The quick brown <b>fox</b> jumped over the lazy <b>dogs</b>"))

  private val extractTds = """<td>.*</td>""".r

  test("renders plain text items") {
    val engine = new TemplateEngine

    val value = engine.render(template, Vector(justRight, tooMany, notEnough))
    assert(value.isValid)
    val documentText = value.getOrElse("invalid")
    val allTds = Vector() ++ extractTds.findAllIn(documentText)

    //    println(documentText)

    allTds shouldBe Vector(
      """<td><span class="insert-word">foo</span></td>""",
      """<td><span class="insert-word">bar</span></td>""",
      """<td><span class="insert-word">baz</span></td>""",
      """<td><span class="insert-word">qux</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="insert-word">quux</span></td>""",
      """<td><span class="insert-word">corge</span></td>""",
      """<td><span class="insert-word">grault</span></td>""",
      """<td><span class="insert-word">garply</span></td>""",
      """<td><span class="insert-word">foo</span></td>""",
      """<td><span class="insert-word">bar</span></td>""",
      """<td><span class="insert-word">baz</span></td>""",
      """<td><span class="insert-word">qux</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="insert-word">quux</span></td>""",
      """<td><span class="insert-word">corge</span></td>""",
      """<td><span class="insert-word">grault</span></td>""",
      """<td><span class="insert-word">garply</span></td>""",
      """<td><span class="insert-word">foo</span></td>""",
      """<td><span class="insert-word">bar</span></td>""",
      """<td><span class="insert-word">baz</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>"""
    )

  }

  test("renders HTML items") {
    val engine = new TemplateEngine

    val value = engine.render(template, Vector(htmlItems))
    assert(value.isValid)
    val documentText = value.getOrElse("invalid")
    val allTds = Vector() ++ extractTds.findAllIn(documentText)

    allTds shouldBe Vector(
      """<td><span class="insert-word"><b>simple</b></span></td>""",
      """<td><span class="insert-word">The quick brown <b>fox</b> jumped over the lazy <b>dogs</b></span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""",
      """<td><span class="insert-word">!!!not enough words provided!!!</span></td>""")

  }

  private lazy val template = SanitizedHtml(
    """
<!DOCTYPE html>
  <html lang="en">
    <head>
      <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
          <title>Template</title>
          <style type="text/css">
            td, th {
              border: 1px solid black;
              text-align: center;
            }
          </style>
          <style type="text/css" media="print">

          </style>
          <body>
            <div class="card-template">
              <table>
                <thead>
                  <tr>
                    <th colspan="3">BINGO</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td><span class="insert-word">This</span></td>
                    <td><span class="insert-word">Will</span></td>
                    <td><span class="insert-word">Be</span></td>
                  </tr>
                  <tr>
                    <td><span class="insert-word">Replaced</span></td>
                    <td><strong>Free Space</strong></td>
                    <td><span class="insert-word"/></td>
                  </tr>
                  <tr>
                    <td><span class="insert-word"/></td>
                    <td><span class="insert-word"/></td>
                    <td><span class="insert-word"/></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </body>
        </html>
""")

  private def buildCardData(data: Iterable[String]): CardData[SanitizedHtml] =
    CardData(data.map(s => Word(SanitizedHtml(s))).toVector)
}
