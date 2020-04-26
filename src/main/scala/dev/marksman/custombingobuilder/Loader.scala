package dev.marksman.custombingobuilder

import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}
import play.api.{ApplicationLoader, Configuration}

class Loader extends GuiceApplicationLoader() {
  override protected def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    val extra = Configuration()
    val config = extra.withFallback(context.initialConfiguration)
    val builder = initialBuilder
      .in(context.environment)
      .loadConfig(config)
      .overrides(overrides(context): _*)

    builder.overrides(new DefaultServerModule)
  }
}

