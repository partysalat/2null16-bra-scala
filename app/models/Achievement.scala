package models

import org.joda.time.DateTime


case class Achievement(
                        name: String,
                        description: String,
                        imagePath: String,
                        id: Option[Int] = None,
                        createdAt: DateTime = DateTime.now(),
                        updatedAt: DateTime = DateTime.now()
                      )

