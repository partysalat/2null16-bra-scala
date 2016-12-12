package repos.achievements


import com.google.inject.{Inject, Singleton}
import models.Achievement
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class AchievementsRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AchievementsTable with HasDatabaseConfigProvider[JdbcProfile] {

  def insert(achievement: Achievement): Future[Int] = db.run {
    tableQueryInc += achievement
  }
  def insertAll(achievements: List[Achievement]) = db.run {
    tableQueryInc ++= achievements
  }
}


