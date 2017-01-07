package repos.drinks

import models.DrinkType.DrinkType
import models.{Drink, DrinkType}
import org.joda.time.DateTime
import play.api.db.slick.HasDatabaseConfigProvider
import repos.BaseTable
import slick.driver.JdbcProfile



private[repos] trait DrinksTable extends BaseTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val drinks = TableQuery[DrinksTable]
  lazy protected val drinksInc = drinks returning drinks.map(_.id)

  implicit lazy val drinksMapper = MappedColumnType.base[DrinkType, String](
    e => e.toString,
    s => DrinkType.withName(s)
  )


  private[DrinksTable] class DrinksTable(tag: Tag) extends Table[Drink](tag, "drinks") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val drinkType: Rep[DrinkType] = column[DrinkType]("type", O.SqlType("VARCHAR(200)"))
    val createdAt: Rep[DateTime] = column[DateTime]("createdAt", O.SqlType("date"))
    val updatedAt: Rep[DateTime] = column[DateTime]("updatedAt", O.SqlType("date"))


    def * = (name, drinkType, id.?, createdAt, updatedAt) <> ((Drink.apply _).tupled, Drink.unapply)
  }

}