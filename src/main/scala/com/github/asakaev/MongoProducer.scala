package com.github.asakaev

import java.util.concurrent.TimeUnit

import com.github.asakaev.shared.collection
import com.mongodb.reactivestreams.client._
import org.bson.Document
import zio._
import zio.clock.Clock
import zio.console._
import zio.duration.Duration
import zio.interop.reactiveStreams._
import zio.stream._

object MongoProducer extends App {

  val document: UIO[Document] =
    IO.effectTotal {
      new Document("name", "MongoDB")
        .append("type", "database")
        .append("count", 1)
        .append("info", new Document("x", 203).append("y", 102))
    }

  val stream: ZStream[Clock, Throwable, Success] =
    Stream.managed(collection).flatMap { c =>
      ZStream
        .repeatEffectWith(document, Schedule.fixed(Duration(5, TimeUnit.SECONDS)))
        .flatMap { doc =>
          c.insertOne(doc).toStream()
        }
    }

  val application: ZIO[Console with Clock, Throwable, Unit] =
    stream
      .tap { s =>
        putStrLn(s.toString)
      }
      .runDrain
      .tapError { e =>
        putStrLn(e.toString)
      }

  def run(args: List[String]): URIO[Console with Clock, Int] =
    application.fold(_ => 1, _ => 0)

}
