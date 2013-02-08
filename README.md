# clawk

Kinda like awk, but Clojure. Insanely dumb at the moment. Reads each line of stdin, evaluates the code, passing the line as `$`, and writes non-nil results to stdout with `println`.

## Usage

```
$ cat project | clawk '(.toUpperCase $)'
(DEFPROJECT CLAWK "0.1.0-SNAPSHOT"
  :DESCRIPTION "FIXME: WRITE DESCRIPTION"
  :URL "HTTP://EXAMPLE.COM/FIXME"
  :LICENSE {:NAME "ECLIPSE PUBLIC LICENSE"
...
```

## To Build

```
  $ lein uberjar && ./make-sh.sh
```

now put `target/clawk` on your path.

## License

Copyright © 2013 Dave Ray

Distributed under the Eclipse Public License, the same as Clojure.
