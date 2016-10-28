package com.saniasutula.models

import spray.json._

case class Post(id: String, message: Option[String])
case class Paging(next: String, previous: String)
case class PostResponse(data: List[Post], paging: Option[Paging])

object PostResponseJsonProtocol extends DefaultJsonProtocol {
  implicit val postFormat = jsonFormat(Post, "id", "message")
  implicit val pagingFormat = jsonFormat(Paging, "next", "previous")
  implicit val postResponseFormat = jsonFormat(PostResponse, "data", "paging")
}