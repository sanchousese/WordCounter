package com.saniasutula.auth

import com.github.t3hnar.bcrypt._
import org.mindrot.jbcrypt.BCrypt

case class AuthInfo(user: User)

case class User(email: String,
                hashedPassword: Option[String] = None,
                topWords: Option[List[String]] = None) {
  def withPassword(password: String): User = copy(hashedPassword = Some(password.bcrypt(generateSalt)))

  def passwordMatches(password: String): Boolean = hashedPassword.exists(hp => BCrypt.checkpw(password, hp))
}

