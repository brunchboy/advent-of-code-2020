(ns advent-of-code-2022.day-3
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_3.txt")
       slurp))

(defn priority
  "Returns the priority of an item."
  [item]
  (let [ascii (int item)]
    (if (>= ascii (int \a))
      (+ 1 (- ascii (int \a)))
      (+ 27 (- ascii (int \A))))))

(defn common-item
  "Returns the item found in both halves of a rucksack."
  [s]
  (->> (split-at (/ (count s) 2) s)
       (map set)
       (apply set/intersection)
       first))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (->> data
        str/split-lines
        (map common-item)
        (map priority)
        (apply +))))

(defn common-item-2
  "Returns the item found in all rucksacks in a group of elves."
  [group]
  (->> group
       (map set)
       (apply set/intersection)
       first))


(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (->> data
        str/split-lines
        (partition 3)
        (map common-item-2)
        (map priority)
        (apply +))))

(def sample-data
  "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw")
