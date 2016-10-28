package com.saniasutula.utils

import com.mongodb.casbah.Imports._

object Mongo {
  val mongoClient = MongoClient("localhost", 27017)
  val db = mongoClient("test")
  val userDb = db("users")

  // TODO: set index unique
}
