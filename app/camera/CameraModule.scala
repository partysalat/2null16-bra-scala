package camera

import com.google.inject.{AbstractModule, Provides, Singleton}
import com.hopding.jrpicam.RPiCamera
import com.typesafe.config.Config
import play.api.Logger

class CameraModule extends AbstractModule {

  protected def configure: Unit = {
  }

  @Provides
  @Singleton
  def getCamera(): Option[RPiCamera] = {
    try {
      val camera = new RPiCamera("./")
      camera.setWidth(500)
      camera.setHeight(500)
      camera.setTimeout(2)
      Some(camera)
    } catch {
      case e: Throwable =>
        Logger.info(s"Camera could not be initialized due to ${e.toString}")
        None
    }
  }

}