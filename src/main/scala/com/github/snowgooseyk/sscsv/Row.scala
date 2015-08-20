package com.github.snowgooseyk.sscsv

import scala.collection.convert.Wrappers.JListWrapper
import com.github.snowgooseyk.sscsv.base.{ Row => JRow }

/**
 * @author snowgooseyk
 */
case class Row(r: JRow) {

  def rownum = r.getRowNumber

  def columns: Seq[String] = JListWrapper(r.getRawColumnValues[String]).toSeq
}
