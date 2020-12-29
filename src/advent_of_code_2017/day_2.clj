(ns advent-of-code-2017.day-2
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]))

(def input
  "The spreadsheet to be chacksummed (the puzzle input)."
  (-> "2017/day_2.txt"
      io/resource
      io/reader
      line-seq))

(defn checksum-line
  "Given a line of the spreadsheet, calculate the difference between the
  largest and smallest values on that line."
  [line]
  (let [vals (edn/read-string (str "[" line "]"))]
    (- (apply max vals) (apply min vals))))

(defn part-1
  "Calculate the checksum of a spreadsheet given the rules for part 1."
  [input]
  (apply + (map checksum-line input)))

(defn find-divisibles
  "Finds two numbers in a line of numbers that can evenly divide each
  other."
  [line]
  (let [vals (edn/read-string (str "[" line "]"))
        pairs (combo/combinations vals 2)]
    (->> pairs
         (map (fn [[a b]]
                   (let [[a b] [(max a b) (min a b)]]
                     (when (zero? (mod a b))
                       [a b]))))
         (filter identity)
         first)))

(defn part-2
  "Solve part 2: Add up the ratios of the only evenly divisible numbers
  in each row."
  [input]
  (->> input
       (map find-divisibles)
       (map #(apply / %))
       (apply +)))
