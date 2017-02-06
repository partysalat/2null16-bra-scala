package camera

import akka.actor.{ActorRef, ActorSystem}
import camera.TakePhotoActor.{StartSchedulingPhotos, StopSchedulingPhotos, TakePhotoForNewsFeed, TakePhotoForStream}
import com.google.inject.name.Named
import com.google.inject.{Inject, Singleton}
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton
class CameraController @Inject()(val system: ActorSystem, @Named("take-photo-actor") val takePhotoActor: ActorRef)(implicit exec: ExecutionContext) extends Controller {
  def takePhoto = Action {
    takePhotoActor ! TakePhotoForNewsFeed()
    Ok("Photo shoot!")
  }
  def startSchedule = Action {
    takePhotoActor ! StartSchedulingPhotos()
    Ok("start photos")
  }
  def stopSchedule = Action {
    takePhotoActor ! StopSchedulingPhotos()
    Ok("stop photos")
  }
}





