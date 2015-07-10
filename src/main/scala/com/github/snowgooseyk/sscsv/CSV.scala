package com.github.snowgooseyk.sscsv

import java.io.{ File, InputStream, OutputStream, FileInputStream }
import java.nio.charset.Charset
import java.util.{ Iterator => JIterator }
import scala.collection.AbstractIterator
import scala.collection.immutable.ListMap
import scala.collection.convert.Wrappers.JListWrapper
import com.github.snowgooseyk.sscsv.base.{ Row => JRow }
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedReadIterator
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedWriteBuffer
import com.github.snowgooseyk.sscsv.base.{ WriteBuffer => JWriteBuffer }
import java.io.FileOutputStream
import scala.util.Random

/**
 * @author snowgooseyk
 */
object CSV {
  def apply(in: InputStream): CSVReader = apply(in, "UTF-8")
  def apply(in: InputStream, encoding: String): CSVReader = apply(in, encoding, true)
  def apply(in: InputStream, autoClose: Boolean): CSVReader = apply(in, "UTF-8", autoClose)
  def apply(in: InputStream, encoding: String, autoClose: Boolean) = new CSVReader(in, encoding, autoClose)
  def apply(out: OutputStream): CSVWriter = apply(out, "UTF-8")
  def apply(out: OutputStream, encoding: String): CSVWriter = apply(out, encoding, true)
  def apply(out: OutputStream, autoClose: Boolean): CSVWriter = apply(out, "UTF-8", autoClose)
  def apply(out: OutputStream, encoding: String, autoClose: Boolean) = new CSVWriter(out, encoding, autoClose)
  def apply(file: File): CSVReader = apply(new FileInputStream(file))
  def apply(file: File, encoding: String): CSVReader = apply(new FileInputStream(file), encoding)
  def apply(fileName: String): CSVReader = apply(new File(fileName))
  def apply(fileName: String, encoding: String): CSVReader = apply(new File(fileName), encoding)
  def into(file: File): CSVWriter = apply(new FileOutputStream(file))
  def into(file: File, encoding: String): CSVWriter = apply(new FileOutputStream(file), encoding)
  def into(fileName: String): CSVWriter = into(new File(fileName))
  def into(fileName: String, encoding: String): CSVWriter = into(new File(fileName), encoding)
}

trait Extractor {
  val iterator: Iterator[Row]

  def asList = iterator.toList.map(r => r.raw)

  def asMapList: List[ListMap[String, String]] = {
    val header = iterator.take(1).flatMap(r => r.raw).toList

    iterator.drop(0).map { r =>
      val m = ListMap(r.raw.zipWithIndex.map { zi =>
        (header(zi._2) -> zi._1)
      }.toSeq: _*)
      if (header.size != m.size) {
        throw new UnbalanceHeaderException(header.size, m.size)
      }
      m
    }.toList
  }
}

class Reader(sep: Char, in: InputStream, encoding: String = "UTF-8", autoClose: Boolean = true) extends Extractor {
  override val iterator = RowIteratorWrapper(new DelimitedReadIterator(sep, in, Charset.forName(encoding), autoClose))
}

case class CSVReader(in: InputStream, encoding: String = "UTF-8", autoClose: Boolean = true) extends Reader(',', in, encoding, autoClose)

sealed case class RowIteratorWrapper(underlying: JIterator[JRow]) extends AbstractIterator[Row] with Iterator[Row] {

  def hasNext = underlying.hasNext

  def next = Row(underlying.next)
}

case class Row(r: JRow) {

  def rownum = r.getRowNumber

  def raw: List[String] = JListWrapper(r.getRawColumnValues[String]).toList
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
