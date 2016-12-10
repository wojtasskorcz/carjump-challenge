package me.carjump.fetcher.actors

import akka.actor.{Actor, Props}
import me.carjump.fetcher.services.{Compressed, Compressor}

class CacheActor(compressor: Compressor) extends Actor {
  import CacheActor._

  var cache = Seq.empty[Compressed[String]]

  def receive = {
    case UpdateCache(items) =>
      cache = compressor.compress(items)

    case GetElement(idx) =>
      sender ! compressor.getAtIndex(cache, idx)
  }

}

object CacheActor {
  case class UpdateCache(items: Seq[String])
  case class GetElement(index: Int)
  def props(compressor: Compressor) = Props(new CacheActor(compressor))
}
