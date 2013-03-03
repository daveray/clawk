(ns clawk.main-test
  (:use clojure.test
        clawk.main))

(defmacro with-in-out-str
  [in & code]
  `(with-out-str (with-in-str ~in ~@code)))

(deftest test-main
  (testing "omits blank lines"
    (is (= "a\nb\n"
           (with-in-out-str
             "a\n    \nb\n"
             (main "$")))))
  (testing "evaluates code in user ns"
    (is (= "user\n"
           (with-in-out-str
             "1"
             (main "(ns-name *ns*)"))))))

(deftest test-delimiter
  (testing "defaults to passing unmodified line as $"
    (is (= "a\nb\nc\n"
           (with-in-out-str
             "a\nb\nc\n"
             (main "$")))))

  (testing "splits lines into a vector with a literal, with all the trimmings"

    (is (= "foo\nbaz\n"
           (with-in-out-str
             "foo ? bar\nbaz?yum"
             (main "-d" "?" "($ 0)")))))

  (testing "splits lines into a vector with a regex, with all the trimmings"
    (is (= "foo\nbaz\n"
           (with-in-out-str
             "foo,bar\nbaz  ,yum"
             (main "-d" "#\",\"" "($ 0)"))))))

(deftest test-init
  (testing "runs some code first"
    (is (= "1\n2\n3\n"
           (with-in-out-str
             "a\nb\nc\n"
             (main "-i" "(def x (atom 0))"
                    "(swap! x inc)"))))))

(deftest test-read
  (testing "(read-string)s each line and passes it as $"
    (is (= "3\n21\n"
           (with-in-out-str
             "[1 2]\n[6 7 8]"
             (main "-r" "(reduce + 0 $)")))))

  (testing "(read-string)s each field and passes it as vector $"
    (is (= "[1 2 3 4]\n[6 7 8 9 10]\n"
           (with-in-out-str
             "[1 2],[3 4]\n[6 7 8], [9 10]"
             (main "-d" "," "-r" "(vec (apply concat $))"))))))

(deftest test-pr
  (testing "(prn)s output instead of println"
    (is (= "\"foo\"\n\"bar\"\n"
           (with-in-out-str
             "foo\nbar"
             (main "-p" "$"))))))

