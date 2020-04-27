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
    val engine = new TemplateEngine(PostProcessor.doNothing)

    val value = engine.render(template, Vector(justRight, tooMany, notEnough))
    assert(value.isValid)
    val documentText = value.getOrElse("invalid")
    val allTds = Vector() ++ extractTds.findAllIn(documentText)

    allTds shouldBe Vector(
      """<td><span class="extra-class">foo</span></td>""",
      """<td><span class="extra-class-1 extra-class-2">bar</span></td>""",
      """<td><span>baz</span></td>""",
      """<td><span>qux</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span>quux</span></td>""",
      """<td><span>corge</span></td>""",
      """<td><span>grault</span></td>""",
      """<td><span>garply</span></td>""",
      """<td><span class="extra-class">foo</span></td>""",
      """<td><span class="extra-class-1 extra-class-2">bar</span></td>""",
      """<td><span>baz</span></td>""",
      """<td><span>qux</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span>quux</span></td>""",
      """<td><span>corge</span></td>""",
      """<td><span>grault</span></td>""",
      """<td><span>garply</span></td>""",
      """<td><span class="extra-class">foo</span></td>""",
      """<td><span class="extra-class-1 extra-class-2">bar</span></td>""",
      """<td><span>baz</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>"""
    )

  }

  test("renders HTML items") {
    val engine = new TemplateEngine(PostProcessor.doNothing)

    val value = engine.render(template, Vector(htmlItems))
    assert(value.isValid)
    val documentText = value.getOrElse("invalid")
    val allTds = Vector() ++ extractTds.findAllIn(documentText)

    allTds shouldBe Vector(
      """<td><span class="extra-class"><b>simple</b></span></td>""",
      """<td><span class="extra-class-1 extra-class-2">The quick brown <b>fox</b> jumped over the lazy <b>dogs</b></span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""",
      """<td><span>!!!not enough words provided!!!</span></td>""")

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
                    <td><span class="insert-word extra-class">This</span></td>
                    <td><span class="insert-word extra-class-1 extra-class-2">Will</span></td>
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
