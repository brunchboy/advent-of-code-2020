(ns advent-of-code-2020.day-6
  "Solutions to the day 6 problems."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The ...? (the puzzle input)."
  (str/split-lines (slurp (io/resource "day_6.txt"))))

(defn part-1
  "Solve part 1."
  []
  (loop [sum   0
         group #{}
         left  input]
    (cond
      (empty? left)
      (+ sum (count group))

      (empty? (first left))
      (recur (+ sum (count group))
             #{}
             (rest left))

      :else
      (recur sum
             (set/union group (set (first left)))
             (rest left)))))

(defn part-2
  "Solve part 2."
  []
  (loop [sum   0
         group #{}
         left  input
         new   true]
    (cond
      (empty? left)
      (+ sum (count group))

      (empty? (first left))
      (recur (+ sum (count group))
             #{}
             (rest left)
             true)

      :else
      (recur sum
             (if new
               (set (first left))
               (set/intersection group (set (first left))))
             (rest left)
             false))))
