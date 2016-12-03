package models


import org.joda.time.DateTime


case class UserId(id: Int) extends AnyVal

case class UserName(name: String) extends AnyVal

case class UserImage(imagePath: String) extends AnyVal

//case class User(id: UserId, userName: UserName, createdAt: DateTime)
case class User(id: Int, userName: String, createdAt: DateTime)
