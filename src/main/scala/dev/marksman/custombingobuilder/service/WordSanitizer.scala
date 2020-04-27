package dev.marksman.custombingobuilder.service

import dev.marksman.custombingobuilder.types.SanitizedHtml
import org.owasp.html.{HtmlPolicyBuilder, PolicyFactory, Sanitizers}

trait WordSanitizer {
  def sanitizeWord(input: String): Option[SanitizedHtml]
}

class DefaultWordSanitizer extends WordSanitizer {
  private val policy = buildOwaspPolicy

  def sanitizeWord(input: String): Option[SanitizedHtml] = {
    val sanitized = policy.sanitize(input.trim).trim
    if (!sanitized.isBlank) Some(SanitizedHtml(sanitized)) else None
  }


  private def buildOwaspPolicy: PolicyFactory = {
    val customPolicies = new HtmlPolicyBuilder()
      .allowCommonInlineFormattingElements()
      .allowCommonBlockElements()
      .allowElements("span")
      .allowStyling()
      .allowTextIn("span")
      .allowAttributes("class").onElements("div", "span", "table", "tr", "th", "td")
      .toFactory;

    customPolicies.and(Sanitizers.TABLES).and(Sanitizers.IMAGES)
  }
}
