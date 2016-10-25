package com.saniasutula

import akka.actor.Actor
import com.saniasutula.auth.{Authenticator, UserDao}
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.routing.HttpService
import spray.routing.directives._


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
          onSuccess(createFuture) { user => complete(s"User ${user.email} was successfully created") }
          onFailure(OnFailureFutureMagnet(createFuture)) {e => complete(Conflict, e.getMessage)}
        }
      } ~ get {
        complete(s"${Mongo.collection.find().toList.mkString("\n\n")}")
      }
    }
}