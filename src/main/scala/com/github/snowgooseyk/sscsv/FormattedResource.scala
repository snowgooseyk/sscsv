package com.github.snowgooseyk.sscsv

import java.io.{ File, InputStream, OutputStream, FileInputStream, FileOutputStream, Reader, BufferedReader, InputStreamReader, StringReader }

/**
 * @author snowgooseyk
 */
trait FormattedResource[R, W] {

  protected[this] val defaultEncoding = "UTF-8"

  // Needed to override them..
  def apply(in: Reader, autoClose: Boolean = true): R
  def apply(out: OutputStream, encoding: String, autoClose: Boolean): W

  // Predefined methods.
  def apply(in: InputStream): R = apply(in, defaultEncoding)
  def apply(in: InputStream, encoding: String): R = apply(in, encoding, true)
  def apply(in: InputStream, autoClose: Boolean): R = apply(in, defaultEncoding, autoClose)
  def apply(in: InputStream, encoding: String, autoClose: Boolean): R = apply(new InputStreamReader(in, encoding), autoClose)
  def apply(file: File): R = apply(file, defaultEncoding)
  def apply(file: File, encoding: String): R = apply(new FileInputStream(file), encoding)
  def apply(fileName: String): R = apply(fileName, defaultEncoding)
  def apply(fileName: String, encoding: String): R = apply(new File(fileName), encoding)
  def from(contents: String, autoClose: Boolean = true): R = apply(new StringReader(contents), autoClose)
  def apply(out: OutputStream): W = apply(out, defaultEncoding)
  def apply(out: OutputStream, encoding: String): W = apply(out, encoding, true)
  def apply(out: OutputStream, autoClose: Boolean): W = apply(out, defaultEncoding, autoClose)
  def into(file: File): W = into(file, defaultEncoding)
  def into(file: File, encoding: String): W = apply(new FileOutputStream(file), encoding)
  def into(fileName: String): W = into(new File(fileName))
  def into(fileName: String, encoding: String): W = into(new File(fileName), encoding)
}
