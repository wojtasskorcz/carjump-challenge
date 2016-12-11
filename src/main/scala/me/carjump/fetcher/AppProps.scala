package me.carjump.fetcher

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

object AppProps {
  private implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration =
    scala.concurrent.duration.Duration.fromNanos(d.toNanos)

  private val config = ConfigFactory.load()

  val fetchInterval: FiniteDuration = config.getDuration("fetch.interval")
  val fetchUrl = config.getString("fetch.url")
  val httpHost = config.getString("http.host")
  val httpPort = config.getInt("http.port")
}
