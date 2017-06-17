package achievements.actors

import achievements.actors.AchievementDrinkType.AchievementDrinkType
import achievements.models.TimingAchievementConstraints
import achievements.repos.AchievementsRepository
import akka.actor.{Actor, Props, Stash}
import com.google.inject.Inject
import drinks.repos.DrinksRepository
import news.repos.NewsRepository
import play.api.Logger
import websocket.WebsocketService

import scala.concurrent.ExecutionContext

object TimingAchievementActor {
  def props(newsRepository: NewsRepository,
            drinksRepository: DrinksRepository,
            achievementsRepository: AchievementsRepository,
            websocketService: WebsocketService)(
      implicit executionContext: ExecutionContext) = {
    Props(
      new TimingAchievementActor(newsRepository,
                                 drinksRepository,
                                 achievementsRepository,
                                 websocketService))
  }

  case class ProcessTimingAchievement(timingAchievementConstraint:TimingAchievementConstraints)

}

class TimingAchievementActor @Inject()(
    newsRepository: NewsRepository,
    drinksRepository: DrinksRepository,
    achievementsRepository: AchievementsRepository,
    websocketService: WebsocketService)(implicit ec: ExecutionContext)
    extends Actor
    with Stash {

  import TimingAchievementActor._
  val logger = Logger(this.getClass)

  def receive = initialReceive

  def initialReceive: Receive = normalReceive

  def normalReceive: Receive = {
    case ProcessTimingAchievement(drinkType) => logger.info(s"Processing fooo ${drinkType.toString}")
    case _ => ()
  }

}
