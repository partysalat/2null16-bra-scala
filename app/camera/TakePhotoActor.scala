package camera

import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File}
import java.util.UUID
import javax.imageio.ImageIO

import akka.actor.Actor
import camera.TakePhotoActor._
import camera.models.NewsImage
import camera.repos.NewsImagesRepository
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.hopding.jrpicam.RPiCamera
import com.migcomponents.migbase64.Base64
import news.models.{News, NewsType}
import news.repos.NewsRepository
import play.api.Logger
import websocket.WebsocketService

import scala.concurrent.{ExecutionContext, Future}


object TakePhotoActor {

  case class TakePhotoForStream()

  case class TakePhotoForNewsFeed()

  case class StopSchedulingPhotos()

  case class StartSchedulingPhotos()

  case class PhotoTakenFailedException(message: String = "", cause: Throwable = null)
    extends Exception(message, cause)

  case class NoCameraException(message: String = "", cause: Throwable = null)
    extends Exception(message, cause)

}

@Singleton
class TakePhotoActor @Inject()(
                                @Named("streamFileName") streamFileName: String,
                                piCamera: Option[RPiCamera],
                                newsReposity: NewsRepository,
                                newsImagesRepository: NewsImagesRepository,
                                websocketService: WebsocketService
                              )(implicit ec: ExecutionContext) extends Actor {
  def receive = idle

  def available: Receive = {
    case TakePhotoForStream() => takeBufferedImageAndPushToClients
    case StopSchedulingPhotos() => context.become(idle)
    case TakePhotoForNewsFeed() =>
      context.become(onHold)
      takePhotoForNewsFeed.onComplete(_ => context.become(available))
    case _ => ()
  }

  def idle: Receive = {
    case StartSchedulingPhotos() => context.become(available)
    case TakePhotoForNewsFeed() => takePhotoForNewsFeed
    case _ => ()
  }

  def onHold: Receive = {
    case _ => ()
  }


  private def takePhotoForNewsFeed: Future[Unit] = {
    val fileName = s"${UUID.randomUUID().toString}.jpg"
    Logger.info(s"Taking photo with name $fileName")
    takePhoto(fileName)
      .flatMap(_ => {
        newsImagesRepository.insertAll(List(NewsImage(fileName))).map(_.head)
      })
      .flatMap(imageId => newsReposity.insert(News(1, NewsType.IMAGE, referenceId = imageId)))
      .map(newsIds => {
        websocketService.notify(Seq(newsIds))
        ():Unit
      })
      .recover({
        case e: PhotoTakenFailedException => {
          Logger.warn(s"Eror occured while taking photo: ${e.toString}")
          (): Unit
        }
        case _: NoCameraException => ()
      })
  }

  private def takePhoto(fileName: String): Future[File] = {
    piCamera.map(camera =>
      Option(camera.takeStill(fileName, 1024, 768)) match {
        case Some(file) => Future.successful(file)
        case None => Future.failed(PhotoTakenFailedException("Photo is null!"))
      }
    ).getOrElse(Future.failed(NoCameraException("No camera attached")))
  }

  private def takeBufferedImageAndPushToClients: Unit = {
    Logger.info(s"TAKE PHTOO ${context.toString}")

    piCamera
      .map(camera => {
        Option(camera.takeBufferedStill())
      })
      .map(toBase64)
      .foreach(base64ImageString => websocketService.notifyReloadPhotoStream(base64ImageString))
  }

  private def toBase64(image: Option[BufferedImage]) = {
    image.map({ bufferedImg =>
      val out: ByteArrayOutputStream = new ByteArrayOutputStream()
      ImageIO.write(bufferedImg, "jpg", out)
      Base64.encodeToString(out.toByteArray, false)
    }).getOrElse("<NO image >")

  }
}
