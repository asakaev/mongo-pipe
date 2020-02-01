package com.github.asakaev

import com.github.asakaev.shared.collection
import com.mongodb.client.model.changestream.ChangeStreamDocument
import org.bson.Document
import zio._
import zio.console._
import zio.interop.reactiveStreams._
import zio.stream._

object MongoConsumer extends App {

  val stream: Stream[Throwable, ChangeStreamDocument[Document]] =
    Stream.managed(collection).flatMap { c =>
      c.watch().toStream()
    }

  val application: ZIO[Console, Throwable, Unit] =
    stream
      .tap { csd =>
        putStrLn(csd.toString)
      }
      .runDrain
      .tapError { e =>
        putStrLn(e.toString)
      }

  def run(args: List[String]): URIO[Console, Int] =
    application.fold(_ => 1, _ => 0)

}
