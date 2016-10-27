package com.saniasutula.auth

import com.mongodb.casbah.commons.MongoDBObject
import com.saniasutula.Mongo

import com.mongodb.casbah.query.Imports._
import scala.concurrent.{ExecutionContext, Future}

object UserDao {
  val collection = Mongo.userDb

  def createUser(email: String, password: String)(implicit ec: ExecutionContext): Future[User] = {
    Future {
      val newUser = User(email).withPassword(password)
      val result = Mongo.userDb.insert(
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

  def getUser(email: String)(implicit ec: ExecutionContext): Option[User] = {
    val query = MongoDBObject("email" -> email)
    val userEntity = collection.findOne(query)
    for {
      u <- userEntity
      email <- u.getAs[String]("email")
      password = u.getAs[String]("password")
      topWords = u.getAs[List[String]]("topWords")
    } yield User(email, password, topWords)
  }

  def userInsertWords(email: String, topWords: Array[String])
                     (implicit ec: ExecutionContext): Option[User] = {
    val query = MongoDBObject("email" -> email)
    val update = $set("topWords" -> topWords)
    collection.update(query, update)
    getUser(email)
  }
}
