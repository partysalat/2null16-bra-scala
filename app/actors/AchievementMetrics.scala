package actors

import scala.collection.mutable.Map

case class AchievementMetrics(
                               properties:Map[String,Property],
                               achievements:Map[String,Property]
                             )

case class Achievement(
                        name: String,
                        props: List[Property],
                        unlocked: Boolean
                      )

object Property {
  val ACTIVE_IF_GREATER_THAN = ">"
  val ACTIVE_IF_LESS_THAN = "="
  val ACTIVE_IF_EQUALS_TO = "<"
}

case class Property(
                     name: String,
                     value: Int,
                     activation: String,
                     activationValue: Int,
                     initialValue: String
                   )