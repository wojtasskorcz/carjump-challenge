package me.carjump.fetcher

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import me.carjump.fetcher.actors.FetcherActor
import me.carjump.fetcher.actors.FetcherActor.Fetch
import org.specs2.mutable.SpecificationLike

import scala.concurrent.duration.FiniteDuration

class FetcherActorSpec extends TestKit(ActorSystem()) with SpecificationLike {

  "FetcherActor" should {
    "correctly fetch data" in {
      val cache = TestProbe()
      val fetcher = system.actorOf(FetcherActor.props(cache.ref))
      fetcher ! Fetch
      val items = cache.expectMsgClass(FiniteDuration(3, TimeUnit.SECONDS), classOf[Seq[String]])
      items must not be empty
      items must contain((s: String) => s.length must_== 1)
    }
  }

}
