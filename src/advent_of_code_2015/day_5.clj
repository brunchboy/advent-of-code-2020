(ns advent-of-code-2015.day-5
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The list of strings to check (the puzzle input)."
  (->> (io/resource "2015/day_5.txt")
       io/reader
       line-seq))

(defn nice?
  "Checks whether a string satisfies the rules of being nice."
  [s]
  (and
   (< 2 (count (filter #{\a \e \i \o \u} s)))
   (some (partial apply =) (partition 2 1 s))
   (not-any? #{'(\a \b) '(\c \d) '(\p \q) '(\x \y)} (partition 2 1 s))))

(defn part-1
  "Solve part 1."
  []
  (count (filter nice? input)))

(defn has-repeated-pair?
  "Checks whether a string has any pair of two adjacent letters which
  occurs more than once with no overlap."
  [s]
  (loop [i 0]
    (let [pair (subs s i (+ i 2))]
      (or (str/includes? (subs s (+ i 2)) pair)
          (when (< i (- (count s) 3))
            (recur (inc i)))))))

(defn has-split-pair?
  "Checks whether a string has a letter which repeats after a gap of one
  letter."
  [s]
  (loop [i 0]
    (or (= (nth s i) (nth s (+ i 2)))
        (when (< i (- (count s) 3))
          (recur (inc i))))))

(defn nice-2?
  "Checks whether a string satisfies the revised rules of being nice for
  part 2."
  [s]
  (and (has-repeated-pair? s) (has-split-pair? s)))

(defn part-2
  "Solve part 2."
  []
  (count (filter nice-2? input)))
