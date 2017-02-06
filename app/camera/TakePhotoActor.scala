package camera

import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, File}
import java.util.UUID
import javax.imageio.ImageIO

import akka.actor.Actor
import camera.TakePhotoActor.{StartSchedulingPhotos, StopSchedulingPhotos, TakePhotoForNewsFeed, TakePhotoForStream}
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

}

@Singleton
class TakePhotoActor @Inject()(
                                @Named("streamFileName") streamFileName: String,
                                piCamera: Option[RPiCamera],
                                newsReposity: NewsRepository,
                                newsImagesRepository: NewsImagesRepository,
                                websocketService: WebsocketService
                              )(implicit ec:ExecutionContext) extends Actor {
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
    Future(takePhoto(fileName))
      .flatMap(_=>{
        Logger.info(s"Insert Image ${NewsImage(fileName)}")
        newsImagesRepository.insertAll(List(NewsImage(fileName))).map(_.head)
      })
      .flatMap(imageId => {
        val news = News(1, NewsType.IMAGE, referenceId = imageId)
        Logger.info(s"Inserted image with news: $news")
        newsReposity.insert(News(1, NewsType.IMAGE, referenceId = imageId))
      })
      .map(newsIds => {
        Logger.info(s"Notify newsId $newsIds ")
        websocketService.notify(Seq(newsIds))
      })
  }

  private def takePhoto(fileName: String): Option[File] = {
    piCamera.flatMap(camera =>
      Option(camera.takeStill(fileName,1024,768))
    )
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
