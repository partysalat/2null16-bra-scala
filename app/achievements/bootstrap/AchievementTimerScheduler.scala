package achievements

import achievements.actors.TimingAchievementActor
import achievements.actors.TimingAchievementActor.ProcessTimingAchievement
import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.markatta.akron.{CronExpression, CronTab}
import play.api.Logger

class AchievementTimerScheduler @Inject()(
    @Named("crontab") crontab: ActorRef,
    @Named(TimingAchievementActor.name) timingAchievementActor: ActorRef
) {
  val logger = Logger(this.getClass)
  logger.info("Initializing timeing achievements...")
  AchievementDefinitionsTiming.achievements
    .foreach { achievementDefinition =>
      crontab ! CronTab.Schedule(
        timingAchievementActor,
        ProcessTimingAchievement(achievementDefinition),
        CronExpression(achievementDefinition.pattern))
    }
}
