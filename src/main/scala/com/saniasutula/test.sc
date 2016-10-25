import com.saniasutula.Mongo
import com.saniasutula.auth.User

import scala.util.Try

// Imports just the Query DSL along with Commons and its dependencies
import com.mongodb.casbah.query.Imports._


val query = MongoDBObject("email" -> "saniasutula12@gmail.com")
val user = Mongo.collection.findOne(query)
for {
  u <- user
  email <- u.getAs[String]("email")
  password = u.getAs[String]("password")
  topWords = u.getAs[List[String]]("top_words")
} yield User(email, password, topWords)

//for {
//  s <- user
//  email <- Try(s.as[String]("email1"))
//} yield email


//user.map(u => {
//  val email: AnyRef = u.get("email")
//  email.map(e => User(e))
//})
//user.as[String]("email")
//
//val res =
//  for {
//    user <- Mongo.collection.findOne(query)
//    email <- user.get("email")
//    pass <- user.get("password")
//    topWords <- user.get("top_words")
////    if pass.toString == "123"
//  } yield User(email = email, pass, topWords)