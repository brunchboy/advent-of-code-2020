(ns advent-of-code-2022.day-13
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_13.txt")
       slurp
       edn/read-string))

(defn fancy-compare
  "Beefed-up version of Clojure's `compare` that handles auto-promoting
  integers to lists containing the integer when needed as specified by
  the problem statement."
  [left right]
  (cond
    (every? number? [left right])
    (compare left right)

    (every? sequential? [left right])
    (loop [[first-left & rest-left]   left
           [first-right & rest-right] right]
      (cond
        (every? nil? [first-left first-right])
        0

        (nil? first-left)
        -1

        (nil? first-right)
        1

        :else
        (let [current (fancy-compare first-left first-right)]
          (if (zero? current)
            (recur rest-left rest-right)
            current))))

    (sequential? left)
    (recur left [right])

    :else
    (recur [left] right)))


(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (->> data
        (partition 2)
        (map (partial apply fancy-compare))
        (interleave (map inc (range)))
        (partition 2)
        (filter (fn [[_ order]] (neg? order)))
        (map first)
        (apply +))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (->> (concat data [[[2]] [[6]]])
        (sort fancy-compare)
        (keep-indexed (fn [idx x] (when (#{[[2]] [[6]]} x) idx)))
        (map inc)
        (apply *))))

(def sample-input
  [[1,1,3,1,1]
   [1,1,5,1,1]

   [[1],[2,3,4]]
   [[1],4]

   [9]
   [[8,7,6]]

   [[4,4],4,4]
   [[4,4],4,4,4]

   [7,7,7,7]
   [7,7,7]

   []
   [3]

   [[[]]]
   [[]]

   [1,[2,[3,[4,[5,6,7]]]],8,9]
   [1,[2,[3,[4,[5,6,0]]]],8,9]])
