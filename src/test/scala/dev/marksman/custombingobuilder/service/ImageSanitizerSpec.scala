package dev.marksman.custombingobuilder.service

import org.jsoup.Jsoup
import org.scalatest.{FunSuite, Matchers}

class ImageSanitizerSpec extends FunSuite with Matchers {

  test("removes images with relative src") {
    val sanitizer = new ImageSanitizer
    val document = Jsoup.parse("<div>A<img src=\"/relative1.jpg\">B<img src=\"relative2.jpg\">C</div>")

    sanitizer.postProcessOutput(document)

    document.select("img").size() shouldBe 0
  }

  test("retains images with absolute src") {
    val sanitizer = new ImageSanitizer
    val document = Jsoup.parse("<div>A<img src=\"//example.com/absolute1.jpg\" width=200 height=100>B<img src=\"//example.com/absolute2.jpg\">C</div>")

    sanitizer.postProcessOutput(document)

    val images = document.select("img")
    images.size() shouldBe 2
    val image1 = images.first()
    image1.attr("src") shouldBe "//example.com/absolute1.jpg"
    image1.attr("width") shouldBe "200"
    image1.attr("height") shouldBe "100"
    images.last().attr("src") shouldBe "//example.com/absolute2.jpg"
  }

}
