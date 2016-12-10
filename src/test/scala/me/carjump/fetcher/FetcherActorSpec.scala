package me.carjump.fetcher

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import me.carjump.fetcher.actors.CacheActor.UpdateCache
import me.carjump.fetcher.actors.FetcherActor
import me.carjump.fetcher.actors.FetcherActor.Fetch
import org.specs2.mutable.SpecificationLike
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._

class FetcherActorSpec extends TestKit(ActorSystem()) with SpecificationLike with NoTimeConversions {

  "FetcherActor" should {
    "correctly fetch data" in {
      val cache = TestProbe("cache")
      val fetcher = system.actorOf(FetcherActor.props(cache.ref), "fetcher")
      fetcher ! Fetch
      val items = cache.expectMsgClass(3.seconds, classOf[UpdateCache]).items
      items must not be empty
      items must contain((s: String) => s.length must_== 1)
    }
  }

  step(TestKit.shutdownActorSystem(system))

}
