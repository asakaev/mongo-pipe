package com.github.asakaev

import java.util.concurrent.TimeUnit

import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client._
import org.bson.Document
import zio._
import zio.console._
import zio.interop.reactiveStreams._
import zio.stream._

object ExceptionExampleApp extends App {

  val settings: MongoClientSettings =
    MongoClientSettings
      .builder()
      .applyToClusterSettings { builder =>
        builder.serverSelectionTimeout(1, TimeUnit.MILLISECONDS)
      }
      .build()

  val stream: Stream[Throwable, Document] =
    MongoClients
      .create(settings)
      .getDatabase("database")
      .getCollection("collection")
      .find()
      .toStream()

  val application: UIO[Unit] =
    stream.catchAllCause { cause =>
      println(s"Stream.failed: $cause")
      Stream.empty
    }.runDrain

  def run(args: List[String]): URIO[Console, Int] =
    application.as(0)

}
