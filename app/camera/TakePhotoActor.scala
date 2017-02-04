package camera

import akka.actor.Actor
import camera.TakePhotoActor.TakePhoto
import com.google.inject.{Inject,Singleton}
import com.google.inject.name.Named
import com.hopding.jrpicam.RPiCamera


object TakePhotoActor {
  case class TakePhoto()
}

@Singleton
class TakePhotoActor @Inject()(@Named("streamFileName") streamFileName:String, piCamera:Option[RPiCamera]) extends Actor {
  def receive = {
    case TakePhoto() => piCamera.map(_.takeStill(streamFileName))
  }

}
