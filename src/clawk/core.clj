(ns clawk.core
  (:require [clojure.java.io :as io])
  (:gen-class))

(defn -main
  [& [code & args]]
  (let [handler (eval `(fn [~'$] ~(read-string code)))]
    (doseq [line (line-seq (io/reader *in*))]
      (when-let [result (handler line)]
        (println result)))))

