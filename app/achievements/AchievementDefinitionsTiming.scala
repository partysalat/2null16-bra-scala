package achievements
import drinks.models.DrinkType._
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
      pattern = "0 8 * * *",
      drinkType = BEER
    )
  )
}