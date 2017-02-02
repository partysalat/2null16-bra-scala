package achievements.actors

import achievements.actors.AchievementCounterType.AchievementCounterType
import achievements.models.Achievement
import drinks.models.DrinkType.DrinkType
import play.api.Logger

import scala.collection.mutable
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
object AchievementCounterType extends Enumeration {
  type AchievementCounterType = Value
  //user specific metrics
  val DRINK_COUNT = Value("ALL_DRINK")
  val BEER = Value("BEER")
  val COCKTAIL = Value("COCKTAIL")
  val SHOT = Value("SHOT")
  val SOFTDRINK = Value("SOFTDRINK")
  implicit class DrinkTypeToAchievementCounterType(drinkType: DrinkType){
    def toCounterType = {
      AchievementCounterType.withName(drinkType.toString)
    }
  }

}

object Property {
  implicit class HigherThan(drinkType: AchievementCounterType){
    def countHigherOrEqualThan(count:Int) = {
      Property.countHigherThanOrEqual(drinkType,count)
    }
  }

  val ACTIVE_IF_GREATER_THAN = ">"
  val ACTIVE_IF_LESS_THAN = "="
  val ACTIVE_IF_EQUALS_TO = "<"


  def countHigherThanOrEqual(drinkType:AchievementCounterType, number:Int)={
    val counterName = s"${drinkType.toString}HigherThan$number"
    countProperties(drinkType)(counterName) = toProperty(counterName,number)
    counterName
  }

  private def toProperty(counterName: String,activationCount: Int):Property = {
    Property(counterName,0,ACTIVE_IF_GREATER_THAN,activationCount)
  }


 // val tmp: Set[(AchievementCounterType, mutable.Map[String, Property])] = AchievementCounterType.values.map(counterType => (counterType,mutable.Map[String,Property]()))
  val countProperties: mutable.Map[AchievementCounterType.Value, mutable.Map[String, Property]] = initPropertyCount

  def initPropertyCount:mutable.Map[AchievementCounterType.Value, mutable.Map[String, Property]]= {
    val tmp1: Seq[(AchievementCounterType, mutable.Map[String, Property])] = AchievementCounterType.values.toSeq.map(counterType => (counterType,mutable.Map[String,Property]()))
    mutable.Map() ++ tmp1.toMap
  }
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