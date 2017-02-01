package achievements

import achievements.models.AchievementsResponse
import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import news.repos.NewsRepository
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class AchievementController @Inject()(actorSystem: ActorSystem, newsRepository: NewsRepository)
                                     (implicit exec: ExecutionContext) extends Controller {
  val logger: Logger = Logger(this.getClass)

  def getAchievements = Action.async {

    newsRepository.getAchievements
      .map(_.groupBy(_._1.userId.get))
      .map(_.mapValues(tuples => AchievementsResponse(tuples.head._2.get, tuples.map(_._3.get))))
      .map(_.map({case (key,value) => (key.toString,Json.toJson(value))}))
      .map(items => Ok(Json.toJson(items)))
      .recoverWith({
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

}





