package achievements.actors


import java.time.LocalDateTime

import achievements.models.TimingAchievementConstraints
import achievements.services.AchievementService
import akka.actor.Actor
import com.google.inject.Inject
import news.repos.NewsRepository
import play.api.Logger

import scala.concurrent.ExecutionContext
object TimingAchievementActor{
  case class ProcessTimingAchievement(
                                       timingAchievementConstraint: TimingAchievementConstraints)

}

class TimingAchievementActor @Inject()(
    newsRepository: NewsRepository,
    achievementService: AchievementService)(implicit ec: ExecutionContext)
    extends Actor {
  import achievements.actors.TimingAchievementActor.ProcessTimingAchievement
  val logger = Logger(this.getClass)

  def receive = normalReceive

  def normalReceive: Receive = {
    case ProcessTimingAchievement(timingAchievementConstraints) =>
      logger.info(s"Processing timing achievement ${timingAchievementConstraints.toString}")
      val now = LocalDateTime.now()
      newsRepository.getDrinkNews
//      timingAchievementConstraints.drinkType
    case _ => ()
  }

}
