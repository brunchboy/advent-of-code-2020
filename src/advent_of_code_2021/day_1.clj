(ns advent-of-code-2021.day-1
  "Solutions for day 1."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn count-increases
  "Count how many times a sequence increases as you move from element to element."
  [xs]
  (->> xs
       (partition 2 1)
       (map (partial apply <))
       (filter identity)
       count))

(def input
  "The puzzle data."
  (->> (io/resource "2021/day_1.edn")
       slurp
       edn/read-string))

(defn part-1
  "Solve part 1: Count how many times the puzzle data sequence increments."
  ([]
   (part-1 input))
  ([data]
   (count-increases data)))

(defn windows
  "Calculate the sums of sliding windows of 3 elements of a sequence."
  [xs]
  (map (partial apply +) (partition 3 1 xs)))

(defn part-2
  "Solve part 2: Count how many times the sum of the sliding windows increment."
  ([]
   (part-2 input))
  ([data]
   (count-increases (windows data))))
