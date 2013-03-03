# clawk

Kinda like awk, but Clojure. Reads each line of stdin, binds it to `$` and evaluates the code provided. Non-nil results go to stdout.

Pros:

* I don't really know awk. I do know Clojure.
* Doing the `reader/line-seq/doseq/split/print` dance in Clojure is tedious.
* If you happen to have files where each record is a clojure data structure, awk and friends don't help.

Cons:

* Awk and friends have many man years put into them. Clawk has a couple man hours.
* JVM Startup time. When the file's big enough, this doesn't bug me much.
* Speed. For a task that awk can do, I can't imagine it not being way, way, way, way faster than this.

I basically see this as a part of a larger pipeline when you, off the top of the your head, you can do something easier with Clawk than with `sed`, `grep`, `cut`, `tr`, and all the others.

## Usage

`$ clawk options expression`

By default, each line of stdin is trimmed and then bound to `$` and then the provided code is evaluated. Blank lines are dropped:

```
$ echo -e "1\n 2 \n \n4\n" | clawk '(identity $)'
1
2
4
```

*of course you could just use `$` and omit `identity`*

### As Clojure Data

The `-r` option applies `clojure.edn/read-string` to the line before binding the value to `$`.

```
$ echo -e "1\n2\n3\n" | clawk -r '(* $ $)'
1
4
9
```

This is pretty nice when you have a Clojure map on each line. `$` becomes your map.

Similarly, the `-p` option applies `prn` to the result of each line rather than `println` making the output `read`-able:

```
# Without -p
$ echo -e "abc\ndef\n" | clawk '{:value $}'
{:value abc}
{:value def}

# With -p
$ echo -e "abc\ndef\n" | clawk -p '{:value $}'
{:value "abc"}
{:value "def"}
```

### Filtering
If your bit of code returns false-y, no output is written:

```
$ echo -e "1\n2\n3\n4\n5\n6\n" | clawk -r '(if (< 4 (* $ $) 30) $)'
3
4
5
```

*and, of course, this is no replacement for `grep` for filtering on regex*

### Delimiters

If you specify a delimiter with `-d`, then each line is split and `$` is bound to the resulting vector:

```
$ echo -e "1,2,3\n4,5,6\n7,8,9\n" | clawk -d ',' '($ 1)'
2
5
8
```

*of course you'd just use `cut` for this*

and combining `-d` with `-r`, `clojure.end/read-string` is applied to each field:

```
$ echo -e "1,2,3\n4,5,6\n7,8,9\n" | clawk -d ',' -r '(reduce * $)'
6
120
504
```

The value passed to `-d` can also be a regex:

```
$ echo -e "foo234bar456yum\nbaz9gar\n" | clawk -d '#"\d+"' '(format "%s-%s" ($ 1) ($ 0))'
bar-foo
gar-baz
```

### Initialization

The `-i` option lets you run some code before processing starts;

```
$ echo -e "2\n3\n4\n5\n6\n" | clawk -r -i '(def acc (atom []))' '(swap! acc conj $)'
[2]
[2 3]
[2 3 4]
[2 3 4 5]
[2 3 4 5 6]
```

*this example indicates that a reduce mode or something is missing from Clawk*

Useful for requiring namespaces or something I think.

## To Build

```
  $ lein uberjar && ./make-sh.sh
```

now put `target/clawk` on your path.

*Note that this is an ugly hack and may not actually work on many systems. In that case, it's back to just using the uberjar or however you like to run Clojure apps*

## License

Copyright © 2013 Dave Ray

Distributed under the Eclipse Public License, the same as Clojure.
