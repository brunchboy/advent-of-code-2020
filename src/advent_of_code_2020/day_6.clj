(ns advent-of-code-2020.day-6
  "Solutions to the day 6 problems."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The groups of answers (the puzzle input)."
  (->> (io/resource "day_6.txt")
       slurp
       str/split-lines
       (partition-by empty?)
       (filter #(not= '("") %))
       (map #(map set %))))

(defn part-1
  "Solve part 1."
  []
  (apply + (map (comp count #(apply set/union %)) input)))

(defn part-2
  "Solve part 2."
  []
  (apply + (map (comp count #(apply set/intersection %)) input)))
