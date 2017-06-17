package achievements

import achievements.actors.TimingAchievementActor.ProcessTimingAchievement
import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import play.api.Logger

class AchievementTimerScheduler @Inject()(
    scheduler: QuartzSchedulerExtension,
    @Named("timingAchievementActor") timingAchievementActor: ActorRef
) {
  val logger = Logger(this.getClass)
  logger.info("Initializing timeing achievements...")
  AchievementDefinitionsTiming.achievements
    .foreach { achievementDefinition =>
      scheduler.schedule(achievementDefinition.pattern,
                         timingAchievementActor,
                         ProcessTimingAchievement(achievementDefinition))
    }

}
