package ch02iomonad.auth

import java.net.URL

case class User(id: Int, name: String)

object User {

  val userFile = "./users.txt"
  val userUrl = new URL("file:" + userFile)

  def parse(str: String): User = {
    val list = str.split(":").toList
    User(list.head.toInt, list.tail.head)
  }

  def findById(userId: Int, users: Seq[User]): Option[User] =
    users.find(_.id == userId)

  val getUsers: Seq[User] =
    linesFromUrl(userUrl).map(User.parse)
}
