package achievements

import achievements.actors.AchievementDrinkType._
import achievements.models.{Achievement, TimingAchievementConstraints}

object AchievementDefinitionsTiming {

  val achievements = List(
    /**
      * Beer
      */
    TimingAchievementConstraints(
      Achievement(
        "Der letzte Kunde",
        "Letztes Bier vor 8 Uhr morgens bestellt",
        "/internal/assets/achievements/moe.png"
      ),
      pattern = "EightInTheMorning",
      drinkType = BEER
    )
  )
}