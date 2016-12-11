package me.carjump.fetcher.services

import org.scalacheck.{Gen, Properties}
import org.scalacheck.Prop.forAll
import org.scalacheck.Arbitrary.arbInt
import org.specs2.mutable.Specification

class RleCompressorSpec extends Specification {
  val compressor = new RleCompressor
  val decompressed = Seq("A", "A", "B", "A", "A", "A")
  val compressed = Seq(Repeat(2, "A"), Single("B"), Repeat(3, "A"))

  "RleCompressor" should {
    "compress items" in {
      compressor.compress(decompressed) must_== compressed
      compressor.compress(Seq.empty[String]) must_== Seq.empty[Compressed[String]]
      compressor.compress(Seq("A")) must_== Seq(Single("A"))
    }
    "decompress items" in {
      compressor.decompress(compressed) must_== decompressed
      compressor.decompress(Seq.empty[Compressed[String]]) must_== Seq.empty[String]
      compressor.decompress(Seq(Single("A"))) must_== Seq("A")
    }
    "retrieve compressed item by index" in {
      (-2 to 7).foreach(idx => compressor.getAtIndex(compressed, idx) must_== decompressed.lift(idx))
      ok
    }
  }
}

class RleCompressorProperties extends Properties("RleCompressor") {
  val compressor = new RleCompressor

  val itemsGen = Gen.containerOf[List,String](Gen.oneOf("A", "B", "C", "D"))

  val repeatedItemsGen = for {
    items <- itemsGen
  } yield items.flatMap(item => Seq(item, item))

  property("compression") = forAll(itemsGen) { (items: List[String]) =>
    compressor.compress(items).size <= items.size
  }

  property("compression") = forAll(repeatedItemsGen) { (items: List[String]) =>
    val compressedSize = compressor.compress(items).size
    (compressedSize == 0 && items.isEmpty) || compressedSize < items.size
  }

  Seq(itemsGen, repeatedItemsGen).foreach { generator =>
    property("decompression") = forAll(generator) { (items: List[String]) =>
      compressor.decompress(compressor.compress(items)) == items
    }
    property("index access") = forAll(generator, arbInt.arbitrary) { (items: List[String], idx: Int) =>
      compressor.getAtIndex(compressor.compress(items), idx) == items.lift(idx)
    }
  }

}
