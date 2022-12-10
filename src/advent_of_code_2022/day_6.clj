(ns advent-of-code-2022.day-6
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_6.txt")
       slurp))

(defn end-of-marker
  "Return the position of the first character following the
start-of-packet marker, which is a series of four distinct characters."
  [s n]
  (loop [i 0]
    (if (= n (count (set (take n (drop i s)))))
      (+ i n)
      (recur (inc i)))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (end-of-marker data 4)))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (end-of-marker data 14)))
