Simple CSV library for Scala
===============================================

[![Build Status](https://travis-ci.org/snowgooseyk/sscsv.svg)](https://travis-ci.org/snowgooseyk/sscsv)
[![Coverage Status](https://coveralls.io/repos/snowgooseyk/sscsv/badge.svg)](https://coveralls.io/r/snowgooseyk/sscsv)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

Supports Scala 2.10+

# Usage

Add SBT dependency.

```scala
"com.github.snowgooseyk" %% "sscsv" % "0.1.2"
```

## Read CSV file

Read [CSV](https://tools.ietf.org/html/rfc4180) formatted data.

```
foo,baa,baz
d,e,f
```

```scala
import java.io.File
import com.github.snowgooseyk.sscsv._

// You can also use InputStream or file name(String).

// List(List(foo,baa,baz),List(d,e,f))
CSV(new File("/home/snowgooseyk/import.csv")).asList

// List(ListMap(foo -> d,baa -> e,baz -> f))
CSV(new File("/home/snowgooseyk/import.csv")).asMapList

// Get scala.collection.Iterator[Seq[String]]
CSV(new File("/home/snowgooseyk/import.csv")).iterator

// Map to some object
// Seq((foo,baa,baz),(d,e,f))
CSV(new File("/home/snowgooseyk/import.csv")).map { columns =>
  Tuple3(columns(0),columns(1),columns(2))
}

// Zip with index
// Seq(
//   (Seq(foo,baa,baz),0),
//   (Seq(d,e,f),1)
// )
CSV(new File("/home/snowgooseyk/import.csv")).zipWithIndex

// Zip with header values
// Seq(Seq((d,foo),(e,baa),(f,baz)))
CSV(new File("/home/snowgooseyk/import.csv")).zipWithHeader

// Zip with header and index
// Seq(
//   (Seq((d,foo),(e,baa),(f,baz)),0)
// )
CSV(new File("/home/snowgooseyk/import.csv")).zipWithHeaderAndIndex

// Print all column value.
// foo 
// baa 
// baz 
// d 
// e 
// f 
CSV(new File("/home/snowgooseyk/import.csv")).foreach(_.foreach(println))
```

This library supports a bit complicated CSV data.

```
aaa,bbb,ccc
"ddd",eee,"Value contains line-separator
 or comma, or double-quote"" that have no problem."
```

```scala
import java.io.File
import com.github.snowgooseyk.sscsv._

// List(List(aaa,bbb,ccc),List(ddd,eee,line-separator or comma, or double-quote" that have no problem.))
CSV(new File("/home/snowgooseyk/import.csv")).asList
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
