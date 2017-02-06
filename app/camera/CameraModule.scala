package camera

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides, Singleton}
import com.hopding.jrpicam.RPiCamera
import com.typesafe.config.Config
import play.api.Logger
import play.api.libs.concurrent.AkkaGuiceSupport

class CameraModule extends AbstractModule with AkkaGuiceSupport{

  protected def configure: Unit = {
    bindActor[TakePhotoActor]("take-photo-actor")
    bind(classOf[PhotoScheduler]).asEagerSingleton()
  }

  @Provides
  @Singleton
  def getCamera(config:Config): Option[RPiCamera] = {
    try {
      val camera = new RPiCamera(config.getString("camera.path"))
      camera.setWidth(500)
      camera.setHeight(281)
      camera.setTimeout(1)
      camera.turnOffThumbnail()
      camera.enableBurst()
      camera.setQuality(75)
      camera.turnOffPreview()
      Some(camera)
    } catch {
      case e: Throwable =>
        Logger.warn(s"Camera could not be initialized due to ${e.toString}")
        None
    }
  }

  @Provides
  @Named("streamFileName")
  def getStreamFileName(config:Config):String = {
    config.getString("camera.filename")
  }

}