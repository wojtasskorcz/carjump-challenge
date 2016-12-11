package me.carjump.fetcher

import akka.actor.ActorSystem
import me.carjump.fetcher.actors.FetcherActor.Fetch
import me.carjump.fetcher.actors.{CacheActor, FetcherActor}
import me.carjump.fetcher.services.{RleCompressor, WebServer}

import scala.concurrent.duration._

object AppConfig extends App {
  private val compressor = new RleCompressor

  private implicit val system = ActorSystem()
  private val cache = system.actorOf(CacheActor.props(compressor), "cache")
  private val fetcher = system.actorOf(FetcherActor.props(cache), "fetcher")
  new WebServer(cache)

  private implicit val ec = system.dispatcher
  system.scheduler.schedule(1.second, AppProps.fetchInterval, fetcher, Fetch)
}
