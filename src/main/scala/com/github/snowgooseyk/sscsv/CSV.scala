package com.github.snowgooseyk.sscsv

import java.io.{ File, InputStream, OutputStream, FileInputStream, FileOutputStream, Reader, BufferedReader, InputStreamReader, StringReader }
import java.nio.charset.Charset
import java.util.{ Iterator => JIterator }
import scala.collection.AbstractIterator
import scala.collection.immutable.ListMap
import scala.collection.convert.Wrappers.JListWrapper
import com.github.snowgooseyk.sscsv.base.{ Row => JRow }
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedReadIterator
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedWriteBuffer
import com.github.snowgooseyk.sscsv.base.{ WriteBuffer => JWriteBuffer }
import scala.util.Random

/**
 * @author snowgooseyk
 */
object CSV {
  private[this] val DEFAULT_ENCODING = "UTF-8"
  def apply(in: InputStream): CSVReader = apply(in, DEFAULT_ENCODING)
  def apply(in: InputStream, encoding: String): CSVReader = apply(in, encoding, true)
  def apply(in: InputStream, autoClose: Boolean): CSVReader = apply(in, DEFAULT_ENCODING, autoClose)
  def apply(in: InputStream, encoding: String, autoClose: Boolean): CSVReader = apply(new InputStreamReader(in, encoding), autoClose)
  def apply(in: Reader, autoClose: Boolean = true) = new CSVReader(new BufferedReader(in), autoClose)
  def apply(file: File): CSVReader = apply(file, DEFAULT_ENCODING)
  def apply(file: File, encoding: String): CSVReader = apply(new FileInputStream(file), encoding)
  def apply(fileName: String): CSVReader = apply(fileName, DEFAULT_ENCODING)
  def apply(fileName: String, encoding: String): CSVReader = apply(new File(fileName), encoding)
  def from(contents: String, autoClose: Boolean = true): CSVReader = apply(new StringReader(contents), autoClose)
  def apply(out: OutputStream): CSVWriter = apply(out, DEFAULT_ENCODING)
  def apply(out: OutputStream, encoding: String): CSVWriter = apply(out, encoding, true)
  def apply(out: OutputStream, autoClose: Boolean): CSVWriter = apply(out, DEFAULT_ENCODING, autoClose)
  def apply(out: OutputStream, encoding: String, autoClose: Boolean) = new CSVWriter(out, encoding, autoClose)
  def into(file: File): CSVWriter = into(file, DEFAULT_ENCODING)
  def into(file: File, encoding: String): CSVWriter = apply(new FileOutputStream(file), encoding)
  def into(fileName: String): CSVWriter = into(new File(fileName))
  def into(fileName: String, encoding: String): CSVWriter = into(new File(fileName), encoding)
}

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

case class CSVReader(in: BufferedReader, autoClose: Boolean = true) extends FormattedReader(',', in, autoClose)

sealed case class RowIteratorWrapper(underlying: JIterator[JRow]) extends Iterator[Row] {

  def hasNext = underlying.hasNext

  def next = Row(underlying.next)
}

case class Row(r: JRow) {

  def rownum = r.getRowNumber

  def columns: Seq[String] = JListWrapper(r.getRawColumnValues[String]).toSeq
}

case class CSVWriter(out: OutputStream, encoding: String = "UTF-8", quote: Boolean = true) {
  val buffer = WriteBuffer(new DelimitedWriteBuffer(encoding, ',', quote, out))
  def ++(value: String) = buffer.++(value)
  def ! = buffer.!
}

case class WriteBuffer(underlying: JWriteBuffer, index: Int = 0) {
  def ++(value: String) = {
    underlying.append(1, value)
    copy
  }
  def !() = {
    underlying.flush
    copy
  }
  def ln() = {
    underlying.scroll
    copy
  }
  private[this] def copy = WriteBuffer(underlying, index + 1)
}
