package camera

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

import akka.actor.Actor
import camera.TakePhotoActor.{StartSchedulingPhotos, StopSchedulingPhotos, TakePhotoForStream}
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.hopding.jrpicam.RPiCamera
import com.migcomponents.migbase64.Base64
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
        .map(camera => {
           Option(camera.takeBufferedStill())
        })
        .map(toBase64)
        .foreach(base64ImageString=>websocketService.notifyReloadPhotoStream(base64ImageString))
    case StopSchedulingPhotos() => context.become(idle)
    case _ => ()
  }

  def idle:Receive = {
    case StartSchedulingPhotos() => context.become(available)
    case _ => ()
  }
  def toBase64(image:Option[BufferedImage]) ={
    image.map({bufferedImg=>
      val out:ByteArrayOutputStream = new ByteArrayOutputStream()
      ImageIO.write(bufferedImg,"jpg",out)
      Base64.encodeToString(out.toByteArray,false)
    }).getOrElse("<NO image >")

  }
}
