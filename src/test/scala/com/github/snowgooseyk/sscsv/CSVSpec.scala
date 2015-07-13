package com.github.snowgooseyk.sscsv

import java.io.{ File, FileInputStream, ByteArrayInputStream, ByteArrayOutputStream }
import java.util.UUID
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.execute._
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
    val resource = new File(getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI)
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

  "Write resource to Stream" in {
    val out = new ByteArrayOutputStream()

    val csv = CSV(out)

    csv ++ "a" ++ "b" ++ "100,000,000" ln () !

    csv ++ "d" ++ "e" ++ "\"f\"" ln () !

    val in = new ByteArrayInputStream(out.toByteArray())
    val actual = CSV(in).asList
    actual must have size 2
    actual(0) must_== List("a", "b", "100,000,000")
    actual(1) must_== List("d", "e", "\"f\"")
  }

  "Write resource to File" in TempDirectory { d =>
    val out = new File(d, "temp.csv")

    out.createNewFile()
    val csv = CSV.into(out.getPath)

    csv ++ "a" ++ "b" ++ "100,000,000" ln ()

    csv ++ "d" ++ "e" ++ "\"f\"" ln ()

    csv !

    val in = new FileInputStream(out)
    val actual = CSV(in).asList
    actual must have size 2
    actual(0) must_== List("a", "b", "100,000,000")
    actual(1) must_== List("d", "e", "\"f\"")
  }

  println("Read 100000 CSV lines.")
  val resource = getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/100T.csv")
  val s = System.currentTimeMillis()
  CSV(resource).asMapList
  val e = System.currentTimeMillis()
  println(s"Erapsed Time: ${e - s} ms.")

  object TempDirectory {
    def apply[R: AsResult](a: File ⇒ R) = {
      val temp = createTemporaryDirectory("")
      try {
        AsResult.effectively(a(temp))
      } finally {
        removeTemporaryDirectory(temp)
      }
    }

    def createTemporaryDirectory(suffix: String): File = {
      val base = new File(new File(System.getProperty("java.io.tmpdir")), "sscsv")
      val dir = new File(base, UUID.randomUUID().toString + suffix)
      dir.mkdirs()
      dir
    }

    /** Removes a directory (recursively). */
    def removeTemporaryDirectory(dir: File): Unit = {
      def recursion(f: File): Unit = {
        if (f.isDirectory) {
          f.listFiles().foreach(child ⇒ recursion(child))
        }
        f.delete()
      }
      recursion(dir)
    }
  }
}
