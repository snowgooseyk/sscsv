package com.github.snowgooseyk.sscsv

/**
 * @author snowgooseyk
 */
class UnbalanceHeaderException(headerSize: Int, columnSize: Int, rowNumber: Int) extends RuntimeException {
  override def getMessage: String = {
    s"Header size is ${headerSize}, but column size is ${columnSize} on row ${rowNumber}.";
  }
}