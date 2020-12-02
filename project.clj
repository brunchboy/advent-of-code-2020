(defproject advent-of-code-2020 "0.1.0-SNAPSHOT"
  :description "Solutions to the 2019 Advent of Code problems"
  :url "https://github.com/brunchboy/advent-of-code-2019"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/math.combinatorics "0.1.6"]
                 [org.clojure/core.async "1.3.610"]
                 [clojure-lanterna "0.9.7"]
                 [jline/jline "3.0.0.M1"]
                 [com.taoensso/timbre "5.1.0"]]
  :main advent-of-code-2020.day-1
  :repl-options {:init-ns advent-of-code-2020.core}
  :jvm-opts [#_"-Xmx30g" "-Xss512m"])
