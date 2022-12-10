(ns advent-of-code-2022.day-4
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_4.txt")
       slurp))

(defn parse-ranges
  [line]
  (let [ranges (str/split line #",")]
    (mapcat (fn [range]
           (let [boundaries (str/split range #"-")]
             (map #(Long/parseLong %) boundaries)))
            ranges)))

(defn fully-contain?
  "Checks if either of the specified ranges fully contains the other."
  [[start-a end-a start-b end-b]]
  (or (and (<= start-a start-b) (>= end-a end-b))
      (and (<= start-b start-a) (>= end-b end-a))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (->> data
        str/split-lines
        (map parse-ranges)
        (map fully-contain?)
        (filter identity)
        count)))

(defn overlap?
  "Checks if the specified ranges overlap at all."
  [[start-a end-a start-b end-b]]
  (and (<= start-a end-b) (<= start-b end-a)))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (->> data
        str/split-lines
        (map parse-ranges)
        (map overlap?)
        (filter identity)
        count)))

(def sample-data
  "2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8")
