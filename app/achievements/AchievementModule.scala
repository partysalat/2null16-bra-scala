package achievements

import achievements.actors.TimingAchievementActor
import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import play.api.libs.concurrent.AkkaGuiceSupport

class AchievementModule extends AbstractModule with AkkaGuiceSupport{

  protected def configure: Unit =  {
    bind(classOf[AchievementTimerScheduler]).asEagerSingleton()
    bindActor[TimingAchievementActor]("timingAchievementActor")
  }

  @Provides
  @Singleton
  def getScheduler(system: ActorSystem): QuartzSchedulerExtension = {
   QuartzSchedulerExtension(system)
  }

}