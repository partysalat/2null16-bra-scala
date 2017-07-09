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
                                 @Named(TakePhotoActor.name) val takePhotoActor: ActorRef,
                                 @Named("streamTimeout") interval:Int,
                                 config:Config
                               )(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    0.microseconds, interval.milliseconds, takePhotoActor, TakePhotoForStream())
}