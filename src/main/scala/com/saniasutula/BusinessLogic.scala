package com.saniasutula

import akka.actor.ActorRefFactory
import com.saniasutula.models.{Post, PostResponse, PostResponseJsonProtocol}
import org.apache.spark.rdd.RDD
import spray.client.pipelining._
import spray.http.{HttpRequest, HttpResponse}
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object BusinessLogic {
  def analiseInfo(date: String, accessToken: String)
                 (implicit ec: ExecutionContext, arf: ActorRefFactory): Future[Array[(String, Int)]] = {
    Future {
      val postResponse = getPosts(
        s"https://graph.facebook.com/v2.8/me/feed?fields=message,created_time" +
          s"&since=$date" +
          s"&access_token=$accessToken"
      )
      val nextURL = postResponse.paging.get.next
      val postMessages = Boot.sc.parallelize(postResponse.data)

      val allWords = tracePosts(nextURL, prepossessWords(postMessages))
      val topWords = allWords.reduceByKey((a, b) => a + b).sortBy(_._2, ascending = false)
      topWords.take(11).tail
    }
  }

  private def tracePosts(url: String, wordCount: RDD[(String, Int)])
                        (implicit ec: ExecutionContext, arf: ActorRefFactory): RDD[(String, Int)] = {
    val postResponse = getPosts(url)
    if (postResponse.paging.isEmpty) {
      wordCount
    } else {
      val nextURL = postResponse.paging.get.next
      val postMessages = Boot.sc.parallelize(postResponse.data)
      val countedWords =
        prepossessWords(postMessages)

      tracePosts(nextURL, countedWords.union(wordCount))
    }
  }

  private def prepossessWords(postMessages: RDD[Post]): RDD[(String, Int)] = {
    postMessages
      .filter(_.message.isDefined)
      .flatMap(_.message)
      .map(_.toLowerCase)
      .flatMap(_.split("""[^а-яА-Яa-zA-Z0-9_]"""))
      .map(w => (w, 1))
      .reduceByKey((a, b) => a + b)
  }

  private def getPosts(url: String)(implicit ec: ExecutionContext, arf: ActorRefFactory): PostResponse = {
    val pipeline: HttpRequest => Future[HttpResponse] = sendReceive
    val response: Future[HttpResponse] = pipeline(Get(url))

    val result: HttpResponse = Await.result(response, 20.minutes)
    result.entity.asString.parseJson.convertTo[PostResponse]
  }

}
