(ns advent-of-code-2020.day-9
  "Solutions to the day 9 problems."
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]))

(def input
  "The numbers coming out of the port (the puzzle input)."
  (->> (io/resource "day_9.txt")
       io/reader
       line-seq
       (map #(Long/parseLong %))))

(defn first-invalid
  "Find the first value that cannot be obtained by adding a pair from
  the specified number of preceding values."
  [preamble input]
  (loop [buffer (take preamble input)
         input  (drop preamble input)]
    (let [next (first input)]
      (if (not (some (fn [pair] (= next pair)) (map #(apply + %) (combo/combinations buffer 2))))
        next
        (when (seq input) (recur (concat (rest buffer) [next]) (rest input)))))))

(defn part-1
  "Solve part 1."
  []
  (first-invalid 25 input))

(defn contiguous-match
  "Find a series of contiguous numbers which add up to the specified
  value."
  [target input]
  (loop [input input
         n    1]
    (let [candidates (take n input)
          sum        (apply + candidates)]
      (cond
        (= target sum)
        candidates

        (< target sum)
        (if (seq (rest input))
          (recur (rest input) 1)
          nil)

        (< n (count input))
        (recur input (inc n))))))

(defn part-2
  "Solve the second part."
  []
  (let [candidates (contiguous-match (part-1) input)]
    (+ (apply min candidates) (apply max candidates))))
