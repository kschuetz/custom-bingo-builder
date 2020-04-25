package dev.marksman.custombingobuilder.service

import dev.marksman.custombingobuilder.types.{PopulatedCard, SanitizedHtml}
import org.scalatest.{FunSuite, Matchers}

class TemplateEngineSpec extends FunSuite with Matchers {

  private val justRight = PopulatedCard(Vector("foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply")).map(SanitizedHtml)
  private val tooMany = PopulatedCard(Vector("foo", "bar", "baz", "qux", "quux", "corge", "grault", "garply", "too-many")).map(SanitizedHtml)
  private val notEnough = PopulatedCard(Vector("foo", "bar", "baz")).map(SanitizedHtml)

  private val htmlItems = PopulatedCard(Vector("<b>simple</b>",
    "The quick brown <b>fox</b> jumped over the lazy <b>dogs</b>")).map(SanitizedHtml)

  private val extractTds = """<td>.*</td>""".r

  test("renders plain text items") {
    val engine = new TemplateEngine

    val value = engine.render(template, Vector(justRight, tooMany, notEnough))
    assert(value.isValid)
    val documentText = value.getOrElse("invalid")
    val allTds = Vector() ++ extractTds.findAllIn(documentText)

    //    println(documentText)

    allTds shouldBe Vector(
      """<td><span class="item">foo</span></td>""",
      """<td><span class="item">bar</span></td>""",
      """<td><span class="item">baz</span></td>""",
      """<td><span class="item">qux</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="item">quux</span></td>""",
      """<td><span class="item">corge</span></td>""",
      """<td><span class="item">grault</span></td>""",
      """<td><span class="item">garply</span></td>""",
      """<td><span class="item">foo</span></td>""",
      """<td><span class="item">bar</span></td>""",
      """<td><span class="item">baz</span></td>""",
      """<td><span class="item">qux</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="item">quux</span></td>""",
      """<td><span class="item">corge</span></td>""",
      """<td><span class="item">grault</span></td>""",
      """<td><span class="item">garply</span></td>""",
      """<td><span class="item">foo</span></td>""",
      """<td><span class="item">bar</span></td>""",
      """<td><span class="item">baz</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>"""
    )

  }

  test("renders HTML items") {
    val engine = new TemplateEngine

    val value = engine.render(template, Vector(htmlItems))
    assert(value.isValid)
    val documentText = value.getOrElse("invalid")
    val allTds = Vector() ++ extractTds.findAllIn(documentText)

    allTds shouldBe Vector(
      """<td><span class="item"><b>simple</b></span></td>""",
      """<td><span class="item">The quick brown <b>fox</b> jumped over the lazy <b>dogs</b></span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><strong>Free Space</strong></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""",
      """<td><span class="item">!!!not enough words provided!!!</span></td>""")

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
                    <td><span class="item">This</span></td>
                    <td><span class="item">Will</span></td>
                    <td><span class="item">Be</span></td>
                  </tr>
                  <tr>
                    <td><span class="item">Replaced</span></td>
                    <td><strong>Free Space</strong></td>
                    <td><span class="item"/></td>
                  </tr>
                  <tr>
                    <td><span class="item"/></td>
                    <td><span class="item"/></td>
                    <td><span class="item"/></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </body>
        </html>
""")


}
