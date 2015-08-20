package com.github.snowgooseyk.sscsv

import java.io.OutputStream
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedWriteBuffer

/**
 * @author snowgooseyk
 */
class FormattedWriter(
    out: OutputStream,
    encoding: String = "UTF-8",
    quote: Boolean = true,
    delimitor: Char = ',') {

  val buffer = WriteBuffer(new DelimitedWriteBuffer(encoding, delimitor, quote, out))
  def ++(value: String) = buffer.++(value)
  def ! = buffer.!
}