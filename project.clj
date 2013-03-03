(defproject clawk "0.1.0-SNAPSHOT"
  :description "Kinda like awk, but Clojure"
  :url "http://github.com/daveray/clawk"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [org.clojure/tools.cli "0.2.2"]]
  :warn-on-reflection true
  :main clawk.main)

