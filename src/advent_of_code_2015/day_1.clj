(ns advent-of-code-2015.day-1
  (:require [clojure.java.io :as io]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2015/day_1.txt")
       slurp))

(defn part-1
  "Solve part 1."
  []
  (- (->> input
          (filter #(= \( %))
          count)
     (->> input
          (filter #(= \) %))
          count)))

(defn step
  "Given the remaining instructions, the current position we've reached
  in the instructions, and the height we've reached in the building,
  returns where we will be after processing the next instruction."
  [[instructions position height]]
  [(rest instructions)
   (inc position)
   (if (empty? instructions)
         height
         (if (= (first instructions) \()
           (inc height)
           (dec height)))])

(defn part-2
  "Solve part 2. Note that this won't terminate if we never go below the
  ground floor, but AoC tends not to supply broken inputs like that."
  []
  (let [[_ position] (first (filter (fn [[_ _ height]] (neg? height)) (iterate step [input 0 0])))]
    position))
