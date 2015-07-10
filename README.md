Simple CSV library for Scala
===============================================

[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

# Usage

Add SBT dependency.

```scala
"com.github.snowgooseyk" %% "sscsv" % "0.1.0"
```

## Read CSV file.

```scala
import java.io,File
import com.github.snowgooseyk.sscsv._

// List(List(foo,baa,baz),List(d,e,f))
CSV(new File("/home/snowgooseyk/companies.csv")).asList

// List(ListMap(foo -> d,baa -> e,baz -> f))
CSV(new File("/home/snowgooseyk/companies.csv")).asMapList

// Get scala.collection.Iterator[com.github.snowgooseyk.sscsv.Row] 
CSV(new File("/home/snowgooseyk/companies.csv")).iterator

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

You can also use InputStream or file name(String).


