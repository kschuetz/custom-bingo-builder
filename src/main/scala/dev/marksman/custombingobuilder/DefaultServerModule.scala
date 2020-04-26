package dev.marksman.custombingobuilder

import com.google.inject.AbstractModule
import com.google.inject.Scopes.SINGLETON
import com.typesafe.config.{Config, ConfigFactory}
import dev.marksman.custombingobuilder.service.{DefaultTemplateSanitizer, TemplateSanitizer}

class DefaultServerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Config]).toInstance(ConfigFactory.load)
    bind(classOf[TemplateSanitizer]).to(classOf[DefaultTemplateSanitizer]).in(SINGLETON)
  }
}