package dev.marksman.custombingobuilder

import com.google.inject.AbstractModule
import com.google.inject.Scopes.SINGLETON
import com.typesafe.config.{Config, ConfigFactory}
import dev.marksman.custombingobuilder.service._

class DefaultServerModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Config]).toInstance(ConfigFactory.load)
    bind(classOf[TemplateSanitizer]).to(classOf[DefaultTemplateSanitizer]).in(SINGLETON)
    bind(classOf[WordSanitizer]).to(classOf[DefaultWordSanitizer]).in(SINGLETON)
    bind(classOf[ImageSanitizer]).in(SINGLETON)
    bind(classOf[PostProcessor]).to(classOf[ImageSanitizer]).in(SINGLETON)
    bind(classOf[ShufflerFactory]).toInstance(DefaultShufflerFactory)
    bind(classOf[SheetGenerator]).in(SINGLETON)
    bind(classOf[Settings]).toProvider(classOf[SettingsProvider]).in(SINGLETON)
  }
}