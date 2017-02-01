package achievements.actors

import achievements.models.Achievement
import drinks.models.DrinkType
import drinks.models.DrinkType.DrinkType

import scala.collection.mutable.Map

case class AchievementMetrics(
                               properties: Map[String, Property] = Map(),
                               achievements: Map[String, AchievementConstraints] = Map()
                             ) {
  def defineProperty(property: Property) = {
    properties(property.name) = property
  }

  def defineAchievement(achievement: AchievementConstraints) = {
    achievements(achievement.achievement.name) = achievement
  }

  def setValue(propertyName: String, value: Int): Unit = {
    properties.get(propertyName).map { property: Property =>
      property.activation match {
        case Property.ACTIVE_IF_GREATER_THAN => if (value > property.value) value else property.value;
        case Property.ACTIVE_IF_LESS_THAN => if (value < property.value) value else property.value
      }
    }.foreach { (newValue: Int) =>
      properties(propertyName).value = newValue
    }
  }

  def addValue(propertyNames: List[String], value: Int) = {
    propertyNames.foreach(propertyName => setValue(propertyName, getValue(propertyName) + value))
  }

  def getValue(propertyName: String) = {
    properties(propertyName).value
  }

  private def unlockAchievement(achievement: AchievementConstraints) = {
    achievement.unlocked = true
  }

  def checkAchievements: List[AchievementConstraints] = {
    val unlockedAchievements = achievements
      .toList.map(_._2)
      .filter(!_.unlocked)
      .filter(achievement =>
        achievement.props.count(properties(_).isActive) == achievement.props.size
      )
    unlockedAchievements.foreach(unlockAchievement)

    unlockedAchievements
  }
}

case class AchievementConstraints(
                        achievement:Achievement,
                        props: List[String],
                        var unlocked: Boolean = false
                      )


object Property {
  import scala.collection.immutable.Map

  implicit class HigherThan(drinkType: DrinkType){
    def countHigherOrEqualThan(count:Int) = {
      Property.higherThan(drinkType,count)
    }
  }

  val ACTIVE_IF_GREATER_THAN = ">"
  val ACTIVE_IF_LESS_THAN = "="
  val ACTIVE_IF_EQUALS_TO = "<"

  def higherThan(drinkType:DrinkType, number:Int)={
    s"${drinkType.toString}HigherThan$number"
  }

  private def toProperty(tuple2: (String, Int)):(String, Property) = {
    (tuple2._1,Property(tuple2._1,0,ACTIVE_IF_GREATER_THAN,tuple2._2))
  }

  private def rangeToPropertyMap(range: Range,drinkType: DrinkType) ={
    range.map(count => higherThan(drinkType,count)->count).toMap
      .map(toProperty)
  }

  val beerProperties: Map[String, Property] = rangeToPropertyMap(1 to 25,DrinkType.BEER)

  val cocktailProperties: Map[String, Property] =rangeToPropertyMap(1 to 25,DrinkType.COCKTAIL)

  val shotProperties: Map[String, Property] = rangeToPropertyMap(1 to 25,DrinkType.SHOT)

  val softdrinkProperties: Map[String, Property] = rangeToPropertyMap(1 to 25,DrinkType.SOFTDRINK)

}

case class Property(
                     name: String,
                     initialValue: Int,
                     activation: String,
                     activationValue: Int,
                     var value: Int = 0
                   ) {

  import Property._

  def isActive: Boolean = {
    activation match {
      case ACTIVE_IF_GREATER_THAN => value >= activationValue
      case ACTIVE_IF_LESS_THAN => value <= activationValue
      case ACTIVE_IF_EQUALS_TO => value == activationValue
    }
  }
}