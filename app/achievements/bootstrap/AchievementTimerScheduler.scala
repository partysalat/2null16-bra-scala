package achievements

import achievements.actors.TimingAchievementActor.ProcessTimingAchievement
import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

class AchievementTimerScheduler @Inject()(
    scheduler: QuartzSchedulerExtension,
    @Named("timingAchievementActor") timingAchievementActor: ActorRef
) {

  AchievementDefinitionsTiming.achievements
    .foreach { achievementDefinition =>
//      scheduler.schedule(achievementDefinition.pattern,
//                         timingAchievementActor,
//                         ProcessTimingAchievement(achievementDefinition.drinkType))
    }

}
