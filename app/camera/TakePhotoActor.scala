package camera

import akka.actor.Actor
import camera.TakePhotoActor.{StartSchedulingPhotos, StopSchedulingPhotos, TakePhotoForStream}
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.hopding.jrpicam.RPiCamera
import play.api.Logger
import websocket.WebsocketService


object TakePhotoActor {
  case class TakePhotoForStream()
  case class StopSchedulingPhotos()
  case class StartSchedulingPhotos()
}

@Singleton
class TakePhotoActor @Inject()(
                                @Named("streamFileName") streamFileName:String,
                                piCamera:Option[RPiCamera],
                                websocketService: WebsocketService
                              ) extends Actor {
  def receive = idle

  def available:Receive = {
    case TakePhotoForStream() =>
      Logger.info(s"TAKE PHTOO ${context.toString}")

      piCamera
        .map(_.takeStill(streamFileName))
        .foreach(_=>websocketService.notifyReloadPhotoStream())
    case StopSchedulingPhotos() => context.become(idle)
    case _ => ()
  }

  def idle:Receive = {
    case StartSchedulingPhotos() => context.become(available)
    case _ => ()
  }
}
