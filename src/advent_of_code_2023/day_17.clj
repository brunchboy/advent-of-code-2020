(ns advent-of-code-2023.day-17
  "Solutions for day 17."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def sample-input
  "The sample data."
  "")

(def input
  "The actual puzzle data."
  (->> (io/resource "2023/day_17.txt")
      slurp
      str/split-lines))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
))
