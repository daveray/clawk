(ns clawk.main
  (:require [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [cli]])
  (:gen-class))

(defn delimiter-arg->splitter
  [^String v]
  (let [[_ pattern] (re-matches #"^#\"(.*)\"$" v)] ; Could probably do this with read-string
    (if pattern
      ; split by regex
      (let [cp (java.util.regex.Pattern/compile pattern)]
        (fn [line]
          (->> (string/split line cp)
               (mapv string/trim))))

      ; split by exact string
      (fn [line]
        (->> (java.util.StringTokenizer. line v) ; ugh
             enumeration-seq
             (mapv string/trim))))))

(defn readerize-splitter
  [splitter {:keys [delimiter read-string] :as opts}]
  (if read-string
    (if delimiter
      (fn [line]
        (mapv edn/read-string (splitter line)))
      (fn [line]
        (edn/read-string (splitter line))))
    splitter))

(defn trim-to-nil
  [s]
  (let [trimmed (string/trim s)]
    (if-not (.isEmpty trimmed)
      trimmed)))

(defn parse-args
  [args]
  (cli args
       ["-h" "--help" "Print this message" :flag true]
       ["-d" "--delimiter"
        "Delimiter used to split each line. defaults to none. A string or #\"regex\""
        :parse-fn delimiter-arg->splitter
        :default nil]
       ["-i" "--init"
        "Init code"]
       ["-r" "--read-string"
        "If present, each line (or field with -d) is read-string'd and bound to $"
        :flag true]
       ["-p" "--print-string"
        "If present, non-nil results are printed with prn instead of println."
        :flag true]

       ; TODO option to not trim lines
       ; TODO option to preserve blank lines
       ; TODO option to omit line endings in output, i.e. use print instead of println
       ; TODO option to write result in columns using delimiter
       ))

(defn main
  [& args]
  (let [[opts [code & trailing] help-string] (parse-args args)]
    (if (:help opts)
      (println help-string)
      ; This *ns* thing seems like witch-craft to me so it's probably wrong/terrible
      (binding [*ns* (create-ns 'user)]
        (refer-clojure)

        (when-let [init (:init opts)]
          (eval (read-string init)))

        (let [splitter (-> (or (:delimiter opts) identity)
                           (readerize-splitter opts))
              printer  (if (:print-string opts) prn println)
              handler  (eval `(fn [~'$] ~(read-string code)))]

          (doseq [line (line-seq (io/reader *in*))]
            (when-let [result (some-> line
                                      trim-to-nil
                                      splitter
                                      handler)]
              (printer result))))))))

(defn -main
  [& args]
  (apply main args)
  (System/exit 0))

