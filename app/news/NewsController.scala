package news

import javax.inject._

import achievements.services.AchievementService
import akka.actor.ActorSystem
import news.models._
import news.repos.NewsRepository
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import websocket.WebsocketService

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class NewsController @Inject()(@Named("websocketSystem") websocketActorSystem: ActorSystem,
                               newsRepository: NewsRepository,
                               achievementService: AchievementService,
                               websocketService: WebsocketService)
                              (implicit exec: ExecutionContext) extends Controller {
  val logger: Logger = Logger(this.getClass)

  def getNews(skip: Int) = Action.async {
    newsRepository.getAll(skip)
      .map((news: List[NewsWithItems]) => {
        Ok(Json.toJson(NewsResponse(news)))
      })
      .recoverWith({
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }


  def createDrinkNews = Action.async(parse.json[CreateDrinkNewsDto]) { request =>
    val drinkId = request.body.drink
    val newsList: List[News] = request.body.users.map(userNews => {
      News(userNews.cardinality, NewsType.DRINK, userId = Some(userNews.id), drinkId = Some(drinkId))
    })

    achievementService.notifyAchievements(newsList).flatMap { _ =>
      Logger.info("Insert newslist")
      newsRepository
        .insertAll(newsList)
        .map(websocketService.notify)
        .map(_ => NoContent)
        .recoverWith({
          case e => Future {
            logger.error(e.toString)
            InternalServerError
          }
        })
    }

  }

  def getBestlistNews = Action.async {
    newsRepository
      .getStats
      .map(r => Ok(Json.toJson(NewsStatsResponse(r))))
      .recoverWith({
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

  def removeNews(newsId: Int) = Action.async {
    newsRepository
      .removeNews(newsId)
      .map(_ => websocketService.notifyNewsRemove(newsId))
      .map(_ => NoContent)
      .recoverWith({
        case e => Future {
          logger.error(e.toString)
          InternalServerError
        }
      })
  }

}





