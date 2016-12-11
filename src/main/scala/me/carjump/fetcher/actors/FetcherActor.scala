package me.carjump.fetcher.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.pattern.pipe
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Sink}
import akka.util.ByteString
import me.carjump.fetcher.AppProps
import me.carjump.fetcher.actors.CacheActor.UpdateCache
import me.carjump.fetcher.services.{LogLevels, Logger}

class FetcherActor(cache: ActorRef) extends Actor {
  import FetcherActor._
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  def receive = {
    case Fetch =>
      val response = Http().singleRequest(HttpRequest(uri = AppProps.fetchUrl))
      response.failed.foreach { t =>
        Logger.log(LogLevels.Error, s"Could not fetch ${AppProps.fetchUrl}. Exception: ${t.getMessage}")
      }
      response.foreach { rsp =>
        rsp.status match {
          case StatusCodes.OK =>
            val items = rsp.entity.dataBytes.via(Framing.delimiter(
              ByteString("\n"), maximumFrameLength = Int.MaxValue, allowTruncation = true
            )).map(_.utf8String).runWith(Sink.seq)
            items.map(UpdateCache) pipeTo cache
          case code => Logger.log(LogLevels.Error, s"Unexpected status code when fetching ${AppProps.fetchUrl}: $code")
        }
      }
  }

}

object FetcherActor {
  case object Fetch
  def props(cache: ActorRef) = Props(new FetcherActor(cache))
}