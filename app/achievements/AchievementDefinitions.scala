package achievements

import achievements.actors.AchievementConstraints
import achievements.actors.Property
import achievements.models.Achievement

object AchievementDefinitions {

  import Property._
  import actors.AchievementCounterType._
  val achievements = List(
    AchievementConstraints(
      Achievement(
        "Moe",
        "1 Bier bestellt",
        "/internal/assets/achievements/moe.png"
      ),
      List(BEER countHigherOrEqualThan 1)
    ),
    AchievementConstraints(
      Achievement(
        "Lenny",
        "5 Bier bestellt",
        "/internal/assets/achievements/lenny.png"
      ),
      List(BEER countHigherOrEqualThan 5)
    ),
    AchievementConstraints(
      Achievement(
        "Carl",
        "10 Bier bestellt",
        "/internal/assets/achievements/carl.png"
      ),
      List(BEER countHigherOrEqualThan 10)
    ),
    AchievementConstraints(
      Achievement(
        "Homer",
        "15 Bier bestellt",
        "/internal/assets/achievements/homer.png"
      ),
      List(BEER countHigherOrEqualThan 15)
    ),

    AchievementConstraints(
      Achievement(
        "Barney",
        "25 Cocktails bestellt",
        "/internal/assets/achievements/barney.png"
      ),
      List(BEER countHigherOrEqualThan 25)
    )
  )
}