package com.github.snowgooseyk.sscsv

import java.util.{ Iterator => JIterator }
import com.github.snowgooseyk.sscsv.base.{ Row => JRow }

/**
 * @author snowgooseyk
 */
sealed case class RowIteratorWrapper(underlying: JIterator[JRow]) extends Iterator[Row] {

  def hasNext = underlying.hasNext

  def next = Row(underlying.next)
}
