package me.carjump.fetcher.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import me.carjump.fetcher.actors.CacheActor.{GetElement, UpdateCache}
import me.carjump.fetcher.services.{Compressed, Compressor, Repeat}
import org.specs2.mock.Mockito
import org.specs2.mutable.SpecificationLike
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._

class CacheActorSpec extends TestKit(ActorSystem()) with ImplicitSender with SpecificationLike with NoTimeConversions
  with Mockito {

  "CacheActor" should {
    "cache and retrieve elements" in {
      val compressor = mock[Compressor]
      val shortSeq = Seq("R", "R", "S", "S")
      val compressedShortSeq = Seq(Repeat(2, "R"), Repeat(2, "S"))
      val longSeq = Seq("R", "R", "R", "S", "S")
      val compressedLongSeq = Seq(Repeat(3, "R"), Repeat(2, "S"))

      compressor.compress returns ((seq: Seq[String]) => seq match {
        case `shortSeq` => compressedShortSeq
        case `longSeq` => compressedLongSeq
        case _ => throw new Exception ("not mocked")
      })

      compressor.getAtIndex returns ((seq: Seq[Compressed[String]], idx: Int) => seq match {
        case Seq() if idx == 4 => None
        case `compressedShortSeq` if idx == 4 => None
        case `compressedLongSeq` if idx == 4 => Some ("S")
        case _ => throw new Exception ("not mocked")
      })

      val cache = system.actorOf(CacheActor.props(compressor), "cache")
      cache ! GetElement(4) // cache uninitialized
      expectMsg(1.second, None)
      cache ! UpdateCache(longSeq)
      cache ! GetElement(4)
      expectMsg(1.second, Some("S"))
      cache ! UpdateCache(shortSeq)
      cache ! GetElement(4)
      expectMsg(1.second, None)
      ok
    }
  }

  step(TestKit.shutdownActorSystem(system))

}
