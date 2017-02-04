package camera

import akka.actor.{ActorRef, ActorSystem}
import camera.TakePhotoActor.TakePhotoForStream
import com.google.inject.Inject
import com.google.inject.name.Named

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class PhotoScheduler @Inject() (val system: ActorSystem, @Named("take-photo-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext)
{
  system.scheduler.schedule(
    0.microseconds, 2.seconds, schedulerActor, TakePhotoForStream())
}