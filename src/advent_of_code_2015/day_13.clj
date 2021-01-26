(ns advent-of-code-2015.day-13
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]))

(def input
  "The list of happiness changes due to seating pairs (the puzzle input)."
  (->> (io/resource "2015/day_13.txt")
       io/reader
       line-seq))

(defn read-happiness
  "Given a list of happiness changes in the format used by the problem
  statement, build a map whose keys are tuples of two people, and
  whose values are the change in happiness experienced by the first
  when seated next to the second."
  [lines]
  (reduce (fn [acc line]
            (if-let [[_ person-1 direction amount person-2]
                     (re-matches #"(.+) would (gain|lose) (.+) happiness units by sitting next to (.+)\." line)]
              (assoc acc [person-1 person-2]
                     (if (= "gain" direction)
                       (Long/parseLong amount)
                       (- (Long/parseLong amount))))
              (throw (Exception. (str "Unable to happiness change line: " line)))))
          {}
          lines))

(defn table-happiness
  "Calculates the total change in happiness caused by a particular
  seating arrangement."
  [happiness arrangement]
  (->> (concat arrangement [(first arrangement)])  ; The table is circular, so first sits next to last.
       (partition 2 1)
       (mapcat (fn [[person-1 person-2]]
                 [(happiness [person-1 person-2] 0)
                  (happiness [person-2 person-1] 0)]))
       (apply +)))

(defn part-1
  "Solve part 1, brute force."
  ([]
   (part-1 input))
  ([lines]
   (let [happiness    (read-happiness lines)
         people       (set (map first (keys happiness)))
         arrangements (combo/permutations people)]
     (reduce (fn [acc arrangement] (max acc (table-happiness happiness arrangement)))
             Long/MIN_VALUE
             arrangements))))

(defn part-2
  "Solve part 2, brute force."
  ([]
   (part-2 input))
  ([lines]
   (let [happiness    (read-happiness lines)
         people       (set (map first (keys happiness)))
         arrangements (combo/permutations (conj people "James"))]
     (reduce (fn [acc arrangement] (max acc (table-happiness happiness arrangement)))
             Long/MIN_VALUE
             arrangements))))
