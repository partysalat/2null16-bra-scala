package actors

import models.Achievement

object AchievementDefinitions {

  import Property._
  val achievements = List(
    AchievementConstraints(
      Achievement(
        "Lenny",
        "5 Bier bestellt",
        "/internal/assets/achievements/lenny.png"
      ),
      List(BEERCOUNT_HIGHER_THAN_5)
    ),
    AchievementConstraints(
      Achievement(
        "Hemmingway",
        "5 Cocktails bestellt",
        "/internal/assets/achievements/hemingway.jpg"
      ),
      List(COCKTAILCOUNT_HIGHER_THAN_5)
    )
  )
}