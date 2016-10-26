package com.saniasutula

import akka.actor.Actor
import com.saniasutula.auth.{Authenticator, User, UserDao}
import spray.http.MediaTypes._
import spray.http.{HttpRequest, HttpResponse, StatusCodes}
import spray.routing.HttpService
import spray.client.pipelining._

import scala.concurrent.Future
import scala.util.{Failure, Success}


class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

trait MyService extends HttpService with Authenticator {
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
    } ~ path("secured") {
      get {
        authenticate(basicUserAuthenticator) { userInfo =>
          complete(s"The user is '${userInfo.user.email}'")
        }
      }
    } ~ path("users") {
      post {
        formFields('email.as[String], 'password.as[String], 'confirmPassword.as[String]) { (email, pass, cp) =>
          if (pass != cp) {
            complete(StatusCodes.BadRequest, "Passwords don't matches")
          } else {
            val createFuture = UserDao.createUser(email, pass)
            onComplete(createFuture) {
              case Success(user: User) =>
                complete(s"User ${user.email} was successfully created")
              case Failure(e: Throwable) =>
                complete(StatusCodes.Conflict, StatusCodes.Conflict.defaultMessage)
            }
          }
        }
      } ~ get {
        complete(s"${Mongo.collection.find().toList.mkString("\n\n")}")
      }
    } ~ path("facebook_fetch") {
      get {
        parameter('code.as[String]) {
          code =>
            val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
            val response: Future[HttpResponse] = pipeline(Get(
              "https://graph.facebook.com/oauth/access_token" +
                s"?client_id=${ConfigUtils.appId}" +
                s"&redirect_uri=${ConfigUtils.domain}/facebook_fetch" +
                s"&client_secret=${ConfigUtils.appSecret}" +
                s"&code=$code")
            )
            onComplete(response) {
              case Success(response: HttpResponse) =>
                val accessTokenString: Option[String] = response.entity.data.asString
                  .split("&")
                  .find(_.contains("access_token"))
                complete(s"${response.entity.data.asString}\n\n${accessTokenString.get}")
              case Failure(e: Throwable) =>
                complete(StatusCodes.InternalServerError, e.getMessage)
            }
        }
      }
    }
}