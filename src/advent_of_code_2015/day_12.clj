(ns advent-of-code-2015.day-12
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def input
  "The elves' spreadsheet (the puzzle input)."
  (->> (io/resource "2015/day_12.txt")
       io/reader
       json/read))

(defn sum
  "Find the sum of all numeric values in a JSON object."
  [o]
  (cond
    (map? o)
    (apply + (map sum (vals o)))

    (vector? o)
    (apply + (map sum o))

    (number? o)
    o

    :else
    0))

(defn part-1
  "Solve part 1 of the problem."
  ([]
   (part-1 input))
  ([json]
   (sum json)))

(defn sum-ignoring-red
  "Find the sum of all numeric values in a JSON object, ignoring any
  objects therein which contain the value \"red\"."
  [o]
  (cond
    (map? o)
    (if (some #(= "red" %) (vals o))
      0  ; Ignore objects which contain the value "red".
      (apply + (map sum-ignoring-red (vals o))))

    (vector? o)
    (apply + (map sum-ignoring-red o))

    (number? o)
    o

    :else
    0))

(defn part-2
  "Solve part 2 of the problem."
  ([]
   (part-2 input))
  ([json]
   (sum-ignoring-red json)))
