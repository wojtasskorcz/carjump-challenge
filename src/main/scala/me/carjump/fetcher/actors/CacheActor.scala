package me.carjump.fetcher.actors

import akka.actor.Actor

class CacheActor extends Actor {

  def receive = {
    case items: Seq[String] =>
  }

}
