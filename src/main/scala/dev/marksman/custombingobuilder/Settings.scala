package dev.marksman.custombingobuilder

import javax.inject.{Inject, Provider}
import play.api.Configuration

object Settings {

  object Keys {
    val Root = "custom-bingo-builder"

    val MaxTemplateSizeBytes = s"$Root.max-template-size-bytes"
    val MaxCardsPerSheet = s"$Root.max-cards-per-sheet"
    val DefaultCardsPerSheet = s"$Root.default-cards-per-sheet"
  }

  val defaultSettings: Settings = Settings(maxCardsPerSheet = 200,
    defaultCardsPerSheet = 10,
    maxTemplateSizeBytes = 32768)
}

case class Settings(maxCardsPerSheet: Int,
                    defaultCardsPerSheet: Int,
                    maxTemplateSizeBytes: Int)

class SettingsProvider @Inject()(config: Configuration) extends Provider[Settings] {
  def get(): Settings = loadFromConfig

  private def loadFromConfig: Settings = {
    val maxCardsPerSheet: Int = config.get[Int](Settings.Keys.MaxCardsPerSheet) max 1
    val defaultCardsPerSheet: Int = (config.get[Int](Settings.Keys.DefaultCardsPerSheet) max 1) min maxCardsPerSheet
    val maxTemplateSizeBytes: Int = config.get[Int](Settings.Keys.MaxTemplateSizeBytes) max 1024
    Settings(maxCardsPerSheet = maxCardsPerSheet, defaultCardsPerSheet = defaultCardsPerSheet,
      maxTemplateSizeBytes = maxTemplateSizeBytes)
  }
}
