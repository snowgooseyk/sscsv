package com.github.snowgooseyk.sscsv

import java.io.{ File, InputStream, OutputStream, FileInputStream, FileOutputStream, Reader, BufferedReader, InputStreamReader, StringReader }

/**
 * @author snowgooseyk
 */
case class CSVReader(in: BufferedReader, autoClose: Boolean = true) extends FormattedReader(',', in, autoClose)

/**
 * @author snowgooseyk
 */
case class CSVWriter(out: OutputStream, encoding: String = "UTF-8", quote: Boolean = true) extends FormattedWriter(out, encoding, quote, ',')

/**
 * @author snowgooseyk
 */
object CSV extends FormattedResource[CSVReader, CSVWriter] {
  override def apply(in: Reader, autoClose: Boolean = true) = new CSVReader(new BufferedReader(in), autoClose)
  override def apply(out: OutputStream, encoding: String, autoClose: Boolean) = new CSVWriter(out, encoding, autoClose)
}
