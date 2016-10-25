package com.saniasutula.auth

import com.mongodb.casbah.commons.MongoDBObject
import com.saniasutula.Mongo

import scala.concurrent.{ExecutionContext, Future}

object UserDao {
  val collection = Mongo.collection

  def createUser(email: String, password: String)(implicit ec: ExecutionContext): Future[User] = {
    Future {
      val newUser = User(email).withPassword(password)
      val result = Mongo.collection.insert(
        MongoDBObject(
          "email" -> newUser.email,
          "password" -> newUser.hashedPassword.get
        )
      )
      if (result.wasAcknowledged()) {
        newUser
      } else {
        throw new Exception("User with such email already exists")
      }
    }
  }
}
