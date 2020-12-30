(ns advent-of-code-2017.day-4
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The list of passphrases to be validated (the puzzle input)."
  (-> "2017/day_4.txt"
      io/resource
      io/reader
      line-seq))

(defn no-repetitions?
  "Checks whether the specified line of input (a string) contains a
  valid passphrase according to the rule of part 1, in other words
  consists of a list of words with no repetitions."
  [line]
  (->> (str/split line #"\s+")
       frequencies
       vals
       (apply max)
       (> 2)))

(defn part-1
  "Counts the valid pass phrases in the input according to the rules of
  part 1."
  [input]
  (->> input
       (filter no-repetitions?)
       count))

(defn no-anagrams?
  "Checks that the specified line of input (a string) contains no words
  which are anagrams of each other. Note that if the no-repetitions
  rule is violated, this one will trivially be violated as well."
  [line]
  (->> (str/split line #"\s+")
       (map sort)
       frequencies
       vals
       (apply max)
       (> 2)))

(defn part-2
  "Counts the valid pass phrases in the input according to the rules of
  part 2."
  [input]
  (->> input
       (filter no-anagrams?)
       count))
