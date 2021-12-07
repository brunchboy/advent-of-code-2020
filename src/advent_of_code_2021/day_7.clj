(ns advent-of-code-2021.day-7
  "Day 7 solutions."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn fuel-to-align
  "Calculate how much fuel is required to align the crabs to the
  specified point."
  [crabs x]
  (apply + (map #(Math/abs (- x %)) crabs )))

(def input
  "The puzzle input."
  (-> (io/resource "2021/day_7.edn")
      slurp
      edn/read-string))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([crabs]
   (apply min (for [x (range (apply min crabs) (inc (apply max crabs)))]
                (fuel-to-align crabs x)))))

(defn move-cost
  "Calculate the fuel cost to move a crab sub the specified distance
  according to the rules of part 2 (this is just calculating the sum
  of the arithmetic series from 1 to the distance)."
  [distance]
  (/ (* distance (inc distance)) 2))

(defn fuel-to-align-2
  "Calculate how much fuel is required to align the crabs to the
  specified point, using part 2 rules."
  [crabs x]
  (apply + (map #(move-cost (Math/abs (- x %))) crabs )))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([crabs]
   (apply min (for [x (range (apply min crabs) (inc (apply max crabs)))]
                (fuel-to-align-2 crabs x)))))
