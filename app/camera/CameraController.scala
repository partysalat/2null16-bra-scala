package camera

import com.google.inject.{Inject, Singleton}
import com.hopding.jrpicam.RPiCamera
import play.api.Logger
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton
class CameraController @Inject()(piCamera:Option[RPiCamera])(implicit exec: ExecutionContext) extends Controller {
  val logger: Logger = Logger(this.getClass)
  def takePhoto=Action {
    piCamera.map(_.takeStill("tmp.jpg"))

    Ok("Photo shoot!")
  }

}





