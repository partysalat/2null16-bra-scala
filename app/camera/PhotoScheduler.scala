package camera

import akka.actor.{ActorRef, ActorSystem}
import camera.TakePhotoActor.TakePhotoForStream
import com.google.inject.Inject
import com.google.inject.name.Named
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class PhotoScheduler @Inject() (
                                 val system: ActorSystem,
                                 @Named("take-photo-actor") val schedulerActor: ActorRef,
                                 config:Config
                               )(implicit ec: ExecutionContext)
{
  val interval = config.getInt("camera.streamTimeout")
  system.scheduler.schedule(
    0.microseconds, interval.seconds, schedulerActor, TakePhotoForStream())
}