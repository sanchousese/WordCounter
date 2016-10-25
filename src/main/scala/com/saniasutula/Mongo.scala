package com.saniasutula

import com.mongodb.casbah.Imports._

object Mongo {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("test")
  val collection = db("users")
}
