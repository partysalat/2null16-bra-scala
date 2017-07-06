package achievements

import achievements.models.{Achievement, AchievementsResponse}
import achievements.repos.AchievementsRepository
import achievements.services.AchievementService
import com.google.inject.{Inject, Singleton}
import news.repos.NewsRepository
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class AchievementController @Inject()(
    newsRepository: NewsRepository,
    achievementsRepository: AchievementsRepository,
    achievementService: AchievementService)(implicit exec: ExecutionContext)
    extends Controller {
  val logger: Logger = Logger(this.getClass)

  def getAchievements = Action.async {

    newsRepository.getAchievements
      .map(_.groupBy(_._1.userId.get))
      .map(_.mapValues(tuples =>
        AchievementsResponse(tuples.head._2.get, tuples.map(_._3.get))))
      .map(_.map({ case (key, value) => (key.toString, Json.toJson(value)) }))
      .map(items => Ok(Json.toJson(items)))
      .recoverWith({
        case e =>
          Future {
            logger.error(e.toString)
            InternalServerError
          }
      })
  }

  def syncAchievements = Action.async {
    achievementsRepository.getAll
      .map { achievements =>
        val achievementsNames = achievements.map(_.name)
        List(
          AchievementDefinitions.achievements.map(_.achievement),
          AchievementDefinitionsTiming.achievements.map(_.achievement)
        ).flatten
          .filter(achievement => !achievementsNames.contains(achievement.name))
      }
      .flatMap(achievementsRepository.insertAll)
      .map(_ => achievementService.killAllActors())
      .map(_ => NoContent)
  }

}
