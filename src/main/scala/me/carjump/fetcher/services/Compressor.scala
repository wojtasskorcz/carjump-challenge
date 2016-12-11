package me.carjump.fetcher.services

import scala.annotation.tailrec

sealed trait Compressed[+A]
case class Single[A](element: A) extends Compressed[A]
case class Repeat[A](count: Int, element: A) extends Compressed[A]

/**
  * Interface for compressing and decompressing sequences of items.
  */
trait Compressor {
  /**
    * Compresses a given sequence
    * @tparam A type of elements in the sequence
    * @return function taking a sequence and returning a corresponding compressed sequence
    */
  def compress[A]: Seq[A] => Seq[Compressed[A]]

  /**
    * Decompresses a given compressed sequence
    * @tparam A type of elements in the original sequence
    * @return function taking a compressed sequence and returning a corresponding decompressed sequence
    */
  def decompress[A]: Seq[Compressed[A]] => Seq[A]

  /**
    * Retrieves an element from a given compressed sequence by its index in the original sequence
    * @tparam A type of elements in the original sequence
    * @return function taking a compressed sequence and an index of an element corresponding to the original sequence
    *         and returning the element under the index or None if it doesn't exist
    */
  def getAtIndex[A]: (Seq[Compressed[A]], Int) => Option[A]
}

class RleCompressor extends Compressor {

  override def compress[A]: (Seq[A]) => Seq[Compressed[A]] = {

    @tailrec
    def go(items: Seq[A], acc: List[Compressed[A]]): Seq[Compressed[A]] = {
      if (items.isEmpty) acc.reverse
      else {
        val element = items.head
        val (repeat, rest) = items.span(_ == element)
        val compressed = if (repeat.size == 1) Single(element) else Repeat(repeat.size, element)
        go(rest, compressed :: acc)
      }
    }

    (items: Seq[A]) => go(items, Nil)
  }

  override def decompress[A]: (Seq[Compressed[A]]) => Seq[A] =
    _.flatMap(_ match {
      case Single(element) => Seq(element)
      case Repeat(count, element) => Seq.fill(count)(element)
    })

  override def getAtIndex[A]: (Seq[Compressed[A]], Int) => Option[A] = {

    @tailrec
    def go(items: Seq[Compressed[A]], idx: Int, seen: Int): Option[A] = {
      if (items.isEmpty || idx < 0) None
      else {
        items.head match {
          case Single(element) => if (idx == seen) Some(element) else go(items.tail, idx, seen + 1)
          case Repeat(count, element) =>
            if (idx >= seen && idx < seen + count) Some(element) else go(items.tail, idx, seen + count)
        }
      }
    }

    (items: Seq[Compressed[A]], idx: Int) => go(items, idx, 0)
  }
}