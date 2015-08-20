package com.github.snowgooseyk.sscsv

import java.io.BufferedReader
import scala.collection.immutable.ListMap
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedReadIterator

/**
 * @author snowgooseyk
 */
class FormattedReader(sep: Char, in: BufferedReader, autoClose: Boolean = true) {

  val rows = RowIteratorWrapper(new DelimitedReadIterator(sep, in, autoClose))

  val iterator: Iterator[Seq[String]] = new scala.collection.Iterator[Seq[String]] {
    def hasNext = rows.hasNext

    def next = rows.next.columns
  }

  def foreach(f: Seq[String] => Unit): Unit = iterator.foreach(f)

  def map[B](f: Seq[String] => B) = iterator.toSeq.map(f)

  def flatMap[B](f: Seq[String] => Seq[B]) = iterator.toSeq.flatMap(f)

  def zipWithIndex = iterator.zipWithIndex.toSeq

  def zipWithHeaderIterator: Iterator[Seq[(String, String)]] = {
    val header = rows.take(1).flatMap(_.columns).toList
    new scala.collection.Iterator[Seq[(String, String)]] {
      def hasNext = rows.hasNext

      def next: Seq[(String, String)] = {
        val row = rows.next
        val columns = row.columns
        if (columns.size != header.size) {
          throw new UnbalanceHeaderException(header.size, columns.size, row.rownum)
        }
        columns.zip(header)
      }
    }
  }

  def zipWithHeader = zipWithHeaderIterator.toSeq

  def zipWithHeaderAndIndex = zipWithHeaderIterator.zipWithIndex.toSeq

  def asMapList = zipWithHeaderIterator.map(x => ListMap(x.map(y => (y._2 -> y._1)).toSeq: _*)).toList

  def asList = rows.toList.map(_.columns)

}
