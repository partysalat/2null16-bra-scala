package achievements.services

import java.util.concurrent.TimeUnit

import achievements.actors.UserAchievementActor
import achievements.actors.UserAchievementActor.ProcessDrinkNews
import achievements.repos.AchievementsRepository
import akka.actor.{ActorNotFound, ActorRef, ActorSystem, PoisonPill}
import akka.util.Timeout
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import drinks.repos.DrinksRepository
import news.models.{News, NewsStats}
import news.repos.NewsRepository
import play.api.Logger
import websocket.WebsocketService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AchievementService @Inject()(
                                    @Named("userAchievementSystem") system: ActorSystem,
                                    newsRepository: NewsRepository,
                                    drinksRepository: DrinksRepository,
                                    achievementsRepository: AchievementsRepository,
                                    websocketService: WebsocketService
                                  )(implicit ec: ExecutionContext) {
  implicit val timeout = Timeout(20, TimeUnit.SECONDS)

  def killAllActors: Unit = {
    system.actorSelection(system / "*") ! PoisonPill
  }

  def notifyAchievements(newsList: List[News]): Future[Unit] = {

    Logger.debug("Get stats for all from newsRepo, initializing actors....")
    Future
      .sequence(
        newsList.filter(_.userId.isDefined).map(ensureActorIsCreated)
      )
      .map(_ => notifyEm(newsList))

  }

  private def notifyEm(newsList: List[News]) = {
    Logger.info("Notify users about achievements")
    newsList.map(ProcessDrinkNews)
      .foreach(drinkNews => system.actorSelection(system / "*") ! drinkNews)
  }

  private def ensureActorIsCreated(news: News): Future[ActorRef] = {
    val actorId = news.userId.get.toString
    system.actorSelection(system / actorId).resolveOne()
      .recoverWith({
        case ActorNotFound(_) =>
          Logger.info(s"No actor found for $actorId, creating one ...")
          for {
            statsForUser <- newsRepository.getStatsForUser(news.userId.get)
            statsForAll <- newsRepository.getStatsForAll
          } yield system.actorOf(UserAchievementActor.props(news.userId.get, statsForUser, statsForAll, newsRepository, drinksRepository, achievementsRepository, websocketService), name = actorId)
      })

  }

}
