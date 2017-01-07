package repos.achievements

import models.Achievement
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import repos.BaseTable
import slick.driver.JdbcProfile

private[repos] trait AchievementsTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val achievements = TableQuery[AchievementsTable]
  lazy protected val achievementsInc = achievements returning achievements.map(_.id)

  private[AchievementsTable] class AchievementsTable(tag: Tag) extends Table[Achievement](tag, "achievements") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val achievementName: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val achievementDescription: Rep[String] = column[String]("description", O.SqlType("VARCHAR(200)"))
    val imagePath: Rep[String] = column[String]("imagePath", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))
    val updatedAt: Rep[DateTime] = column[DateTime]("updatedAt", O.SqlType("date"))


    def * = (achievementName, achievementDescription, imagePath, id.?, createdAt, updatedAt) <> ((Achievement.apply _).tupled, Achievement.unapply)
  }

}