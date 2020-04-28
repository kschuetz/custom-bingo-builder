package dev.marksman.custombingobuilder.service

import org.jsoup.nodes.Element

class ImageSanitizer extends PostProcessor {
  def postProcessOutput(element: Element): Unit = {
    val images = element.select("img")
    images.removeIf(img => hasAbsoluteSrc(img))
    images.remove()
  }

  private def hasAbsoluteSrc(element: Element): Boolean =
    element.attr("src").startsWith("//")

}
