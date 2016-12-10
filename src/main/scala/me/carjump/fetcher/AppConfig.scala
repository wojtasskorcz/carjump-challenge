package me.carjump.fetcher

import akka.actor.{ActorSystem, Props}
import me.carjump.fetcher.actors.{CacheActor, FetcherActor}
import me.carjump.fetcher.actors.FetcherActor.Fetch

import scala.concurrent.duration._

object AppConfig {

  private val system = ActorSystem()
  private val cache = system.actorOf(Props[CacheActor])
  private val fetcher = system.actorOf(FetcherActor.props(cache))

  private implicit val ec = system.dispatcher
  system.scheduler.schedule(1.second, AppProps.fetchInterval, fetcher, Fetch)
}
