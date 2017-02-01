package achievements.repos

import achievements.models.Achievement
import com.google.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class AchievementsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AchievementsTable with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._
  def insert(achievement: Achievement): Future[Int] = db.run {
    achievementsInc += achievement
  }
  def insertAll(achievements: List[Achievement]) = db.run {
    achievementsInc ++= achievements
  }
  def getByName(name:String): Future[Achievement] = db.run {
    achievements.filter(_.achievementName === name).result.head
  }
}


