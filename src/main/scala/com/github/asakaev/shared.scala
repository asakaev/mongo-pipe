package com.github.asakaev

import com.mongodb.reactivestreams.client._
import org.bson.Document
import zio._

object shared {

  val mongoClient: Managed[Throwable, MongoClient] =
    Managed.makeEffect(
      MongoClients.create("mongodb://root:example@localhost:27017/?authSource=admin")
    )(_.close())

  val collection: Managed[Throwable, MongoCollection[Document]] =
    mongoClient.map { mc =>
      mc.getDatabase("example").getCollection("example-collection")
    }

}
