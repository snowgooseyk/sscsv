Simple CSV library for Scala
===============================================

[![Build Status](https://travis-ci.org/snowgooseyk/sscsv.svg)](https://travis-ci.org/snowgooseyk/sscsv)
[![Coverage Status](https://coveralls.io/repos/snowgooseyk/sscsv/badge.svg)](https://coveralls.io/r/snowgooseyk/sscsv)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

Supports Scala 2.10+

# Usage

Add SBT dependency.

"com.github.snowgooseyk" %% "sscsv" % "0.1.2"
```

## Read CSV file

Read [CSV](https://tools.ietf.org/html/rfc4180) formatted data.

```
foo,bar,baz
d,e,f
```

```scala
import com.github.snowgooseyk.sscsv._

// List(List(foo,bar,baz),List(d,e,f))
CSV("/home/snowgooseyk/import.csv").asList

// List(ListMap(foo -> d,bar -> e,baz -> f))
CSV("/home/snowgooseyk/import.csv").asMapList

// Get scala.collection.Iterator[Seq[String]]
CSV("/home/snowgooseyk/import.csv").iterator

// Map to some object
// Seq((foo,bar,baz),(d,e,f))
CSV("/home/snowgooseyk/import.csv").map { columns =>
  Tuple3(columns(0),columns(1),columns(2))
}

// Zip with index
// Seq(
//   (Seq(foo,bar,baz),0),
//   (Seq(d,e,f),1)
// )
CSV("/home/snowgooseyk/import.csv").zipWithIndex

// Zip with header values
// Seq(
//   Seq((d,foo),(e,bar),(f,baz))
// )
CSV("/home/snowgooseyk/import.csv").zipWithHeader

// Zip with header and index
// Seq(
//   (Seq((d,foo),(e,bar),(f,baz)),0)
// )
CSV("/home/snowgooseyk/import.csv").zipWithHeaderAndIndex

// Print all column value.
// foo 
// bar 
// baz 
// d 
// e 
// f 
CSV("/home/snowgooseyk/import.csv").foreach(_.foreach(println))

// You can also use java.io.File, java.io.InputStream ,java.io.Reader and CSV formatted String.
// List(List(foo,bar,baz),List(d,e,f))
import java.io.File
CSV(new File("/home/snowgooseyk/import.csv")).asList

// List(List(foo,bar,baz),List(d,e,f))
import java.io.FileInputStream
CSV(new FileInputStream(new File("/home/snowgooseyk/import.csv"))).asList

// List(List(foo,bar,baz),List(d,e,f))
import java.io.FileReader
CSV(new FileReader(new File("/home/snowgooseyk/import.csv"))).asList

// List(List(foo,bar,baz),List(d,e,f))
val csv:String = "foo,bar,baz" + scala.util.Properties.lineSeparator + "d,e,f"
CSV.from(csv).asList
```

This library supports a dirty CSV.

```
aaa,bbb,ccc
"ddd",eee,"Value contains line-separator
 or comma, or double-quote"" that have no problem."
```

```scala
import java.io.File
import com.github.snowgooseyk.sscsv._

// List(List(aaa,bbb,ccc),List(ddd,eee,line-separator or comma, or double-quote" that have no problem.))
CSV("/home/snowgooseyk/import.csv").asList
```

## Write CSV file

```scala
import java.io.File
import com.github.snowgooseyk.sscsv._

val csv = CSV.into(new File("/home/snowgooseyk/export.csv"))

// Appends(++) data and flush(!) file per line(ln).
csv ++ "foo" ++ "baa" ++ "baz" ln() !
csv ++ "d" ++ "e" ++ "f" ln() !

// Show the outputs
// "foo","baa","baz"
// "d","e","f"
```
