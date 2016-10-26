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
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    } ~ path("secured") {
      authenticate(basicUserAuthenticator) { userInfo =>
        complete(s"The user is '${userInfo.user.email}'")
      }
    } ~ path("users") {
      post {
        formFields('email.as[String], 'pass.as[String]) { (email, pass) =>
          val createFuture = UserDao.createUser(email, pass)
          onComplete(createFuture) {
            case Success(user: User) =>
              complete(s"User ${user.email} was successfully created")
            case Failure(e: Throwable) =>
              complete(StatusCodes.Conflict, StatusCodes.Conflict.defaultMessage)
          }
        }
      } ~ get {
        complete(s"${Mongo.collection.find().toList.mkString("\n\n")}")
      }
    } ~ path("login") {
      get {
        parameter('code.as[String]) {
          code =>
            val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
            val response: Future[HttpResponse] = pipeline(Get(
              "https://graph.facebook.com/oauth/access_token" +
                "?client_id=1143682429056274" +
                "&redirect_uri=http://ce5f7db3.ngrok.io/login" +
                "&client_secret=8da071cfe1e38c1cb5cbfea9e1d97127" +
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