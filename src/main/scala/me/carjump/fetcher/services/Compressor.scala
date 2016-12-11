package me.carjump.fetcher.services

import scala.annotation.tailrec

sealed trait Compressed[+A]
case class Single[A](element: A) extends Compressed[A]
case class Repeat[A](count: Int, element: A) extends Compressed[A]

trait Compressor {
  def compress[A]: Seq[A] => Seq[Compressed[A]]
  def decompress[A]: Seq[Compressed[A]] => Seq[A]
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