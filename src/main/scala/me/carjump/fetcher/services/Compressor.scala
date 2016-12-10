package me.carjump.fetcher.services

sealed trait Compressed[+A]
case class Single[A](element: A) extends Compressed[A]
case class Repeat[A](count: Int, element: A) extends Compressed[A]

trait Compressor {
  def compress[A]: Seq[A] => Seq[Compressed[A]]
  def decompress[A]: Seq[Compressed[A]] => Seq[A]
  def getAtIndex[A]: (Seq[Compressed[A]], Int) => Option[A]
}

class RleCompressor extends Compressor {
  override def compress[A]: (Seq[A]) => Seq[Compressed[A]] = _.map(Single(_))

  override def decompress[A]: (Seq[Compressed[A]]) => Seq[A] = _.map(_.asInstanceOf[Single[A]].element)

  override def getAtIndex[A]: (Seq[Compressed[A]], Int) => Option[A] =
    (compressedItems, idx) => compressedItems.lift(idx).map(_.asInstanceOf[Single[A]].element)
}