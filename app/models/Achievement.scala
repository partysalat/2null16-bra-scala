package models

import org.joda.time.DateTime


case class Achievement(
                        achievementName: String,
                        description: String,
                        imagePath: String,
                        createdAt: DateTime,
                        id: Option[Int] = None
                      )
