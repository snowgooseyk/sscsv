package com.github.snowgooseyk.sscsv

import java.io.{ File, InputStreamReader, FileInputStream, ByteArrayInputStream, ByteArrayOutputStream }
import java.util.UUID
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.execute._
import org.specs2.runner.JUnitRunner
import java.io.FileReader

/**
 * @author snowgooseyk
 */
@RunWith(classOf[JUnitRunner])
class CSVSpec extends Specification {

  "CSV" should {
    "Read as List" in {
      val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI.getPath
      val actual = CSV(resource).asList
      actual must have size 3
      actual(0) must contain(exactly("Hoge", "Baa", "Foo", "Baz"))
      actual(1) must contain(exactly("test1", "test２", "test3", "100,000,000"))
      actual(2) must contain(exactly("test4", "test5", "test\"test6", "te,st\"test7"))
    }

    "Read as Iterator" in {
      val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI.getPath
      val actual = CSV(resource).iterator
      actual.hasNext must beTrue
      actual.next must contain(exactly("Hoge", "Baa", "Foo", "Baz"))
      actual.hasNext must beTrue
      actual.next must contain(exactly("test1", "test２", "test3", "100,000,000"))
      actual.hasNext must beTrue
      actual.next must contain(exactly("test4", "test5", "test\"test6", "te,st\"test7"))
      actual.hasNext must beFalse
    }

    "Read and map to tuple" in {
      val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI.getPath
      val actual = CSV(resource).map { x =>
        Tuple4(x(0), x(1), x(2), x(3))
      }
      actual.size must_== 3
      actual(0) must_== Tuple4("Hoge", "Baa", "Foo", "Baz")
      actual(1) must_== Tuple4("test1", "test２", "test3", "100,000,000")
      actual(2) must_== Tuple4("test4", "test5", "test\"test6", "te,st\"test7")
    }

    "Read ,flatten and map to tuple" in {
      val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec2.csv").toURI.getPath
      val actual = CSV(resource, "Shift-JIS").flatMap { x =>
        Seq(Tuple3(x(0), x(1), x(2)))
      }
      actual must have size 4
      actual(0) must_== Tuple3("field_name1", "field_name2", "field_name3")
      actual(1) must_== Tuple3("aaa", "bbb", "ccc")
      actual(2) must_== Tuple3("ddd", "eee", "Value contains line-separator or comma, or double-quot\" that have no problem.")
      actual(3) must_== Tuple3("zzz", "テスト", "xxx")
    }

    "Read and zip with index" in {
      val resource = "AAA,BBB,CCC,\"100,000\",\"テスト\"" + util.Properties.lineSeparator + "DDD,EEE,FFF,\"G\nG\",\"HHH\""
      val actual = CSV.from(resource).zipWithIndex
      actual must have size 2
      actual(0)._1 must contain(exactly("AAA", "BBB", "CCC", "100,000", "テスト"))
      actual(0)._2 must_== 0
      actual(1)._1 must contain(exactly("DDD", "EEE", "FFF", "GG", "HHH"))
      actual(1)._2 must_== 1
    }

    "Read and zip with Header" in {
      val resource = "AAA,BBB,CCC,\"100,000\",\"テスト\"" + util.Properties.lineSeparator + "DDD,EEE,FFF,\"G\nG\",\"HHH\""
      val actual = CSV.from(resource).zipWithHeader
      actual must have size 1
      actual(0) must contain(exactly(("DDD", "AAA"), ("EEE", "BBB"), ("FFF", "CCC"), ("GG", "100,000"), ("HHH", "テスト")))
    }

    "Read and zip with empty resource" in {
      val resource = ""
      val actual = CSV.from(resource).zipWithHeader
      actual must have size 0
    }

    "Read and zip with Header only" in {
      val resource = "AAA,BBB,CCC"
      val actual = CSV.from(resource).zipWithHeader
      actual must have size 0
    }

    "Read and zip with Header and index" in {
      val resource = getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec2.csv").toURI.getPath
      val actual = CSV(resource, "Shift-JIS").zipWithHeaderAndIndex
      actual must have size 3
      actual(0)._1 must contain(exactly(("aaa", "field_name1"), ("bbb", "field_name2"), ("ccc", "field_name3")))
      actual(0)._2 must_== 0
      actual(1)._1 must contain(exactly(("ddd", "field_name1"), ("eee", "field_name2"), ("Value contains line-separator or comma, or double-quot\" that have no problem.", "field_name3")))
      actual(1)._2 must_== 1
      actual(2)._1 must contain(exactly(("zzz", "field_name1"), ("テスト", "field_name2"), ("xxx", "field_name3")))
      actual(2)._2 must_== 2
    }

    "Read as MapList" in {
      val resource = new File(getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI)
      val actual = CSV(resource).asMapList
      actual must have size 2
      actual(0) must havePairs("Hoge" -> "test1", "Baa" -> "test２", "Foo" -> "test3", "Baz" -> "100,000,000")
      actual(1) must havePairs("Hoge" -> "test4", "Baa" -> "test5", "Foo" -> "test\"test6", "Baz" -> "te,st\"test7")
    }

    "Read as MapList from Reader" in {
      val resource = new InputStreamReader(new FileInputStream(new File(getClass.getResource("/com/github/snowgooseyk/sscsv/CSVSpec1.csv").toURI)), "UTF-8")
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
      actual(3) must contain(exactly("zzz", "テスト", "xxx"))
    }

    "Read from reader" in {
      val resource = new InputStreamReader(getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/CSVSpec2.csv"), "Shift-JIS")
      val actual = CSV(resource).asList
      actual must have size 4
      actual(0) must contain(exactly("field_name1", "field_name2", "field_name3"))
      actual(1) must contain(exactly("aaa", "bbb", "ccc"))
      actual(2) must contain(exactly("ddd", "eee", "Value contains line-separator or comma, or double-quot\" that have no problem."))
      actual(3) must contain(exactly("zzz", "テスト", "xxx"))
    }

    "Read from String" in {
      val resource = "AAA,BBB,CCC,\"100,000\",\"テスト\""
      val actual = CSV.from(resource).asList
      actual must have size 1
      actual(0) must contain(exactly("AAA", "BBB", "CCC", "100,000", "テスト"))
    }

    "Read from String as List" in {
      val resource = "AAA,BBB,CCC,\"100,000\",\"テスト\"" + util.Properties.lineSeparator + "DDD,EEE,FFF,\"G\nG\",\"HHH\""
      val actual = CSV.from(resource).asList
      actual must have size 2
      actual(0) must contain(exactly("AAA", "BBB", "CCC", "100,000", "テスト"))
      actual(1) must contain(exactly("DDD", "EEE", "FFF", "GG", "HHH"))
    }

    "Read from String as ListMap" in {
      val resource = "head1,head2,\"head,3\"" + util.Properties.lineSeparator + "AAA,BBB,CCC" + util.Properties.lineSeparator + "DDD,\"E,E,E\",\"F\nF\""
      val actual = CSV.from(resource).asMapList
      actual must have size 2
      actual(0) must contain(exactly("head1" -> "AAA", "head2" -> "BBB", "head,3" -> "CCC"))
      actual(1) must contain(exactly("head1" -> "DDD", "head2" -> "E,E,E", "head,3" -> "FF"))
    }

    "Read from Shift-JIS String" in {
      val resource = new String("AAA,BBB,CCC,\"100,000\",\"テスト\"".getBytes("Shift-JIS"))
      val actual = CSV.from(resource).asList
      actual must have size 1
      actual(0) must contain(exactly("AAA", "BBB", "CCC", "100,000", new String("テスト".getBytes("Shift-JIS"))))
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
      val resource = getClass.getResourceAsStream("/com/github/snowgooseyk/sscsv/CSVSpec4.csv")
      CSV(resource).asMapList must throwA[UnbalanceHeaderException]("Header size is 1, but column size is 3 on row 2.")
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
