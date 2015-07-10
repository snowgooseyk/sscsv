package com.github.snowgooseyk.sscsv

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

/**
 * @author snowgooseyk
 */
@RunWith(classOf[JUnitRunner])
class CSVSpec extends Specification {

  "Read as List" in {
    val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI.getPath
    val actual = CSV(resource).asList
    actual must have size 3
    actual(0) must contain(exactly("Hoge", "Baa", "Foo", "Baz"))
    actual(1) must contain(exactly("test1", "test２", "test3", "100,000,000"))
    actual(2) must contain(exactly("test4", "test5", "test\"test6", "te,st\"test7"))
  }

  "Read as MapList" in {
    val resource = new java.io.File(getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI)
    val actual = CSV(resource).asMapList
    actual must have size 2
    actual(0) must havePairs("Hoge" -> "test1", "Baa" -> "test２", "Foo" -> "test3", "Baz" -> "100,000,000")
    actual(1) must havePairs("Hoge" -> "test4", "Baa" -> "test5", "Foo" -> "test\"test6", "Baz" -> "te,st\"test7")
  }

  "Read resource that contains line-separator and quoted-comma" in {
    val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec2.csv").toURI.getPath
    val actual = CSV(resource, "Shift-JIS").asList
    actual must have size 4
    actual(0) must contain(exactly("field_name1", "field_name2", "field_name3"))
    actual(1) must contain(exactly("aaa", "bbb", "ccc"))
    actual(2) must contain(exactly("ddd", "eee", "Value contains line-separator or comma, or double-quot\" that have no problem."))
    actual(3) must contain(exactly("zzz", "yyy", "xxx"))
  }

  "Read resource without header" in {
    val resource = getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/CSVSpec3.csv")
    val actual = CSV(resource).asList
    actual must have size 3
    actual(0) must_== List("", "", "", "")
    actual(1) must contain(exactly("テスト1", "テスト２", "テスト3", "100,000,000"))
    actual(2) must contain(exactly("test4", "test5,", "test\" ,test6", ",test\"test7"))
  }

  "Read resource without header as Map" in {
    val resource = getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/CSVSpec3.csv")
    CSV(resource).asMapList must throwA[UnbalanceHeaderException]
  }

  "Read unambigous resource without header" in {
    val resource = getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/CSVSpec4.csv")
    val actual = CSV(in = resource, autoClose = false).asList
    resource.close
    actual must have size 4
    actual(0) must contain(exactly("End quote\""))
    actual(1) must contain(exactly("End quote1\"", "End quote2\"", "End quot3\""))
    actual(2) must contain(exactly("End doule quote1\"\"", "End double quote2\"\""))
    actual(3) must contain(exactly("\"Surround with quote1\"", "\"Surround with quote2\""))
  }

  println("Read 100000 CSV lines.")
  val resource = getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/100T.csv")
  val s = System.currentTimeMillis()
  CSV(resource).asMapList
  val e = System.currentTimeMillis()
  println(s"Erapsed Time: ${e - s} ms.")

}
