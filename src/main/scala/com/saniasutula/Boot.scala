package com.saniasutula

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark.{SparkConf, SparkContext}

import scala.concurrent.duration._

object Boot extends App {
  implicit val system = ActorSystem("on-spray-can")
  implicit val timeout = Timeout(20.minutes)

  val service = system.actorOf(Props[MyServiceActor], "dataroot-service")

  val conf = new SparkConf().setAppName("Dataroot message analizer").setMaster("local")
  val sc = new SparkContext(conf)
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
