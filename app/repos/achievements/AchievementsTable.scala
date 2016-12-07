package repos.achievements

import models.Achievement
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import repos.BaseTable
import slick.driver.JdbcProfile

private[repos] trait AchievementsTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val tableQuery = TableQuery[AchievementsTable]
  lazy protected val tableQueryInc = tableQuery returning tableQuery.map(_.id)

  private[AchievementsTable] class AchievementsTable(tag: Tag) extends Table[Achievement](tag, "achievements") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val achievementName: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val achievementDescription: Rep[String] = column[String]("description", O.SqlType("VARCHAR(200)"))
    val imagePath: Rep[String] = column[String]("imagePath", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))


    def * = (achievementName, achievementDescription, imagePath, createdAt, id.?) <> ((Achievement.apply _).tupled, Achievement.unapply)
  }

}