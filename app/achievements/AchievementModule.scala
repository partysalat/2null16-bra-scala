package achievements

import achievements.actors.TimingAchievementActor
import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.markatta.akron.CronTab
import play.api.libs.concurrent.AkkaGuiceSupport

class AchievementModule extends AbstractModule with AkkaGuiceSupport{

  protected def configure: Unit =  {
    bind(classOf[AchievementTimerScheduler]).asEagerSingleton()
    bindActor[TimingAchievementActor]("timingAchievementActor")
  }

  @Provides
  @Singleton
  @Named("crontab")
  def getScheduler(system: ActorSystem): ActorRef = {
   system.actorOf(CronTab.props, "crontab")
  }

}