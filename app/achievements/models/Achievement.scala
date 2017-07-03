package achievements.models

import drinks.models.DrinkType.DrinkType
import org.joda.time.DateTime
import play.api.libs.json._
import users.models.User


case class Achievement(
                        name: String,
                        description: String,
                        imagePath: String,
                        id: Option[Int] = None,
                        createdAt: DateTime = DateTime.now(),
                        updatedAt: DateTime = DateTime.now()
                      )
object Achievement {
  implicit val achievementFormat: Format[Achievement] = Json.format[Achievement]
}
case class AchievementsResponse(
                        user: User,
                        achievements:List[Achievement]
                      )
object AchievementsResponse {
  implicit val achievementsResponseFormat: Format[AchievementsResponse] = Json.format[AchievementsResponse]
}

trait Constraints{

}

case class AchievementConstraints(
                                   achievement: Achievement,
                                   props: List[String],
                                   var unlocked: Boolean = false
                                 ) extends Constraints
case class TimingAchievementConstraints(
                                         achievement: Achievement,
                                         pattern:String,
                                         drinkType: DrinkType,
                                         var unlocked: Boolean = false
                                 ) extends Constraints
