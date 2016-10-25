package com.saniasutula.auth

import com.mongodb.casbah.commons.MongoDBObject
import com.saniasutula.Mongo
import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.directives.AuthMagnet

import scala.concurrent.{ExecutionContext, Future}

trait Authenticator {
  def basicUserAuthenticator(implicit ec: ExecutionContext): AuthMagnet[AuthInfo] = {
    def validateUser(userPass: Option[UserPass]): Option[AuthInfo] = {
//      for {
//        p <- userPass
//        query = MongoDBObject("email" -> p.user)
//        user <- Mongo.collection.findOne(query)
//        if user
//      }
//      userPass.map(_.)
//      for {
//        p <- userPass
//        user <- ApiUser("Gogi", Option("fdsafdsfas"))
//        if user.passwordMatches(p.pass)
//      } yield new AuthInfo(user)
//      Option(ApiUser("Gogi", Option("fdsafdsfas")))
      None
    }

    def authenticator(userPass: Option[UserPass]): Future[Option[AuthInfo]] = Future { validateUser(userPass) }

    BasicAuth(authenticator _, realm = "Private API")
  }
}
