import com.mongodb.DBObject
import com.mongodb.casbah.commons.MongoDBObject
import com.saniasutula.Mongo

val query = MongoDBObject("email" -> "saniasutula@gmail.com")
//val user = Mongo.collection.findOne(query)


val res =
  for {
    user <- Mongo.collection.findOne(query)
    pass = user.get("password")
    if pass.toString == "123"
  } yield user