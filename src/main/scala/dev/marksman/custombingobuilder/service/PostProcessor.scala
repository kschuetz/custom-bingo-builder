package dev.marksman.custombingobuilder.service

import org.jsoup.nodes.Element

trait PostProcessor {
  self =>
  def postProcessOutput(output: Element): Unit

  def andThen(other: PostProcessor): PostProcessor = new PostProcessor {
    def postProcessOutput(output: Element): Unit = {
      self.postProcessOutput(output)
      other.postProcessOutput(output)
    }
  }

}

object PostProcessor {
  val doNothing: PostProcessor = new PostProcessor {
    def postProcessOutput(output: Element): Unit = {}
  }
}