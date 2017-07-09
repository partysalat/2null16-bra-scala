package achievements.actors

import achievements.models.{Achievement, TimingAchievementConstraints}
import achievements.services.AchievementService
import akka.actor.Actor
import com.google.inject.Inject
import news.services.NewsService
import org.joda.time.DateTime
import play.api.Logger
import users.models.User

import scala.concurrent.{ExecutionContext, Future}
object TimingAchievementActor {
  final val name = "timingAchievementActor"
  case class ProcessTimingAchievement(
      timingAchievementConstraint: TimingAchievementConstraints)

}

class TimingAchievementActor @Inject()(
    newsService: NewsService,
    achievementService: AchievementService)(implicit ec: ExecutionContext)
    extends Actor {
  import achievements.actors.TimingAchievementActor.ProcessTimingAchievement
  val logger = Logger(this.getClass)

  def receive = normalReceive

  def normalReceive: Receive = {
    case ProcessTimingAchievement(timingAchievementConstraints) =>
      logger.info(
        s"Processing timing achievement ${timingAchievementConstraints.toString}")
      val now = DateTime.now()
      val currentSender = sender()
      newsService
        .getLatestUserWithType(timingAchievementConstraints.drinkType, now)
        .map(toUsers)
        .flatMap { users =>
          haveUsersAchievementReached(users,
                                      timingAchievementConstraints.achievement)
        }
        .map { users =>
          logger.info(s"users $users unlocked achievement ${timingAchievementConstraints.achievement}")
          users.map { user =>
            achievementService.unlockAchievements(
              user.id.get,
              Seq(timingAchievementConstraints.achievement))
          }

        }
        .map { _ =>
          currentSender ! "ACK"
        }

    case _ => ()
  }
  def toUsers(usersOpt: Seq[Option[User]]): Seq[User] = {
    usersOpt
      .filter(_.isDefined)
      .map(_.get)
  }
  private def haveUsersAchievementReached(
      users: Seq[User],
      achievement: Achievement): Future[Seq[User]] = {
    Future
      .sequence(users.map(hasAchievementReached(achievement, _)))
      .map { achievementReached =>
        logger.info(s"users reached: $achievementReached")
        achievementReached
          .filter(!_._2)
          .map(_._1)
      }
  }
  private def hasAchievementReached(achievement: Achievement, user: User) = {
    newsService
      .hasAchievementReached(achievement, user.id.get)
      .map((user, _))

  }

}
