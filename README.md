Simple CSV library for Scala
===============================================

[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

# Usage

Add SBT dependency.

```scala
"com.github.snowgooseyk" %% "sscsv" % "0.1.0"
```

## Read CSV file.

Read [CSV](https://tools.ietf.org/html/rfc4180) formatted data from resource.

```
foo,baa,baz
d,e,f
```

```scala
import java.io,File
import com.github.snowgooseyk.sscsv._

// You can also use InputStream or file name(String).

// List(List(foo,baa,baz),List(d,e,f))
CSV(new File("/home/snowgooseyk/import.csv")).asList

// List(ListMap(foo -> d,baa -> e,baz -> f))
CSV(new File("/home/snowgooseyk/import.csv")).asMapList

// Get scala.collection.Iterator[com.github.snowgooseyk.sscsv.Row] 
CSV(new File("/home/snowgooseyk/import.csv")).iterator

// Print all column value.
// foo 
// baa 
// baz 
// d 
// e 
// f 
CSV(new File("/home/snowgooseyk/companies.csv")).iterator.foreach { r =>
  r.raw.foreach (c => println(c))
}
```

This library supports complex CSV formatted data from resource.

```
aaa,bbb,ccc
"ddd",eee,"Value contains line-separator
 or comma, or double-quote"" that have no problem."
```

```scala
import java.io,File
import com.github.snowgooseyk.sscsv._

// List(List(aaa,bbb,ccc),List(ddd,eee,line-separator or comma, or double-quote" that have no problem.))
CSV(new File("/home/snowgooseyk/import.csv")).asList
```

## Write CSV file.

```scala
import java.io,File
import com.github.snowgooseyk.sscsv._

val csv = CSV.into(new File("/home/snowgooseyk/export.csv")

// Appends(++) data and flushes(!) outputstream per line.
csv ++ "foo" ++ "baa" ++ "baz" ln() !
csv ++ "d" ++ "e" ++ "f" ln() !

// Show the outputs
// "foo","baa","baz"
// "d","e","f"
```
