package ch02iomonad2.auth

import java.net.URL

case class Password(userId: Int, password: String)

object Password {

  val passwordFile = "passwords.txt"
  val passwordUrl = new URL("file:" + passwordFile)

  def parse(str: String): Password = {
    val list = str.split(":").toList
    Password(list.head.toInt, list.tail.head)
  }

  val getPasswords: Seq[Password] =
    linesFromUrl(passwordUrl).map(Password.parse)
}
