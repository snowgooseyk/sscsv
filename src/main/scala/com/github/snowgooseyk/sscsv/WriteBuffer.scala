package com.github.snowgooseyk.sscsv

import com.github.snowgooseyk.sscsv.base.{ WriteBuffer => JWriteBuffer }
import com.github.snowgooseyk.sscsv.base.dsv.DelimitedWriteBuffer

/**
 * @author snowgooseyk
 */
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
