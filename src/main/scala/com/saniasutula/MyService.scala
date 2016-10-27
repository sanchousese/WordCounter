package com.saniasutula

import akka.actor.{Actor, ActorRefFactory}
import com.saniasutula.auth.{Authenticator, User, UserDao}
import spray.client.pipelining._
import spray.http.MediaTypes._
import com.github.nscala_time.time.Imports._
import spray.http.{HttpRequest, HttpResponse, StatusCodes}
import spray.routing.HttpService

import scala.concurrent.Future
import scala.util.{Failure, Success}


class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

trait MyService extends HttpService with Authenticator {
  implicit val arf: ActorRefFactory = actorRefFactory
  implicit val executorContext = actorRefFactory.dispatcher

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            com.saniasutula.html.index.render.toString
          }
        }
      }
    } ~ path("sign-up") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            com.saniasutula.html.signUp.render.toString
          }
        }
      }
    } ~ path("user") {
      post {
        formFields('email.as[String], 'password.as[String], 'confirmPassword.as[String]) { (email, pass, cp) =>
          if (pass != cp) {
            complete(StatusCodes.BadRequest, "Passwords don't matches")
          } else {
            val createFuture = UserDao.createUser(email, pass)
            GmailSender.installRegularlyMail(email)
            onComplete(createFuture) {
              case Success(user: User) =>
                complete(s"User ${user.email} was successfully created")
              case Failure(e: Throwable) =>
                complete(StatusCodes.Conflict, StatusCodes.Conflict.defaultMessage)
            }
          }
        }
      } ~ get {
        respondWithMediaType(`text/html`) {
          authenticate(basicUserAuthenticator) { userInfo =>
            complete(
              com.saniasutula.html.userPage.render(
                userInfo.user.email,
                userInfo.user.topWords.getOrElse(Nil),
                s"https://www.facebook.com/dialog/oauth?client_id=${ConfigUtils.appId}" +
                  s"&redirect_uri=${ConfigUtils.domain}/facebook_fetch"
              ).toString
            )
          }
        }
      }
    } ~ path("word_top") {
      respondWithMediaType(`text/html`) {
        parameter('access_token.as[String]) { token =>
          authenticate(basicUserAuthenticator) { userInfo =>
            val date = (DateTime.now - 1.year).toString("yyyy-MM-dd")
            val future = BusinessLogic.analiseInfo(date, token)
            onComplete(future) {
              case Success(words: Array[(String, Int)]) =>
                val user = UserDao.userInsertWords(userInfo.user.email, words.map(_._1))
                complete(
                  com.saniasutula.html.userPage.render(
                    user.get.email,
                    user.get.topWords.getOrElse(Nil),
                    s"word_top?access_token=$token"
                  ).toString
                )
              case Failure(e: Throwable) =>
                complete(StatusCodes.InternalServerError, e.getMessage)
            }
          }
        }
      }
    } ~ path("facebook_access_token") {
      get {
        parameter('code.as[String]) {
          code =>
            val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
            val response: Future[HttpResponse] = pipeline(Get(
              "https://graph.facebook.com/oauth/access_token" +
                s"?client_id=${ConfigUtils.appId}" +
                s"&redirect_uri=${ConfigUtils.domain}/facebook_access_token" +
                s"&client_secret=${ConfigUtils.appSecret}" +
                s"&code=$code")
            )
            onComplete(response) {
              case Success(response: HttpResponse) =>
                val accessTokenString: Option[String] = response.entity.data.asString
                  .split("&")
                  .find(_.contains("access_token"))
                complete(s"${response.entity.data.asString}\n\n${accessTokenString.getOrElse("")}")
              case Failure(e: Throwable) =>
                complete(StatusCodes.InternalServerError, e.getMessage)
            }
        }
      }
    }
}