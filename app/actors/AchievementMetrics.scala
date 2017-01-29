package actors

import scala.collection.mutable.Map

case class AchievementMetrics(
                               properties: Map[String, Property] = Map(),
                               achievements: Map[String, Achievement] = Map()
                             ) {
  def defineProperty(property: Property) = {
    properties(property.name) = property
  }

  def defineAchievement(achievement: Achievement) = {
    achievements(achievement.name) = achievement
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

  private def unlockAchievement(achievement: Achievement) = {
    achievement.unlocked = true
  }

  def checkAchievements: List[Achievement] = {
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

case class Achievement(
                        name: String,
                        props: List[String],
                        var unlocked: Boolean = false
                      )

object Property {
  val ACTIVE_IF_GREATER_THAN = ">"
  val ACTIVE_IF_LESS_THAN = "="
  val ACTIVE_IF_EQUALS_TO = "<"

  val BEERCOUNT_HIGHER_THAN_5 = Property("beerCountHigherThan5", 0, ACTIVE_IF_GREATER_THAN, 5)
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
      case ACTIVE_IF_GREATER_THAN => value > activationValue
      case ACTIVE_IF_LESS_THAN => value < activationValue
      case ACTIVE_IF_EQUALS_TO => value == activationValue
    }
  }
}