(ns advent-of-code-2020.day-3
  "Solutions to the day 3 problems."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def trees
  "The pattern of trees, which repeats indefinitely to the right."
  (str/split-lines (slurp (io/resource "day_3.txt"))))

(defn part-1
  "Calculate the number of trees encountered following the given slope."
  [right down]
  (loop [x   0
         y   0
         hit 0]
    (let [x   (mod (+ x right) (count (first trees)))
          y   (+ y down)
          hit (+ hit (if (= (get-in trees [y x] nil) \#) 1 0))]
      (if (< y (count trees))
        (recur x y hit)
        hit))))

(defn part-2
  "Calculate the sum of the specified slopes."
  []
  (* (part-1 1 1)
     (part-1 3 1)
     (part-1 5 1)
     (part-1 7 1)
     (part-1 1 2)))
