package achievements.actors

import achievements.actors.AchievementDrinkType.AchievementDrinkType
import achievements.repos.AchievementsRepository
import akka.actor.{Actor, Props, Stash}
import drinks.repos.DrinksRepository
import news.repos.NewsRepository
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

  case class ProcessTimingAchievement(drinkType: AchievementDrinkType)

}

class TimingAchievementActor(
    newsRepository: NewsRepository,
    drinksRepository: DrinksRepository,
    achievementsRepository: AchievementsRepository,
    websocketService: WebsocketService)(implicit ec: ExecutionContext)
    extends Actor
    with Stash {

  import TimingAchievementActor._

  def receive = initialReceive

  def initialReceive: Receive = normalReceive

  def normalReceive: Receive = {
    case ProcessTimingAchievement(drinkType) => ()
    case _ => ()
  }

}
