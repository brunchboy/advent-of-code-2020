(ns advent-of-code-2022.day-2
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_2.txt")
       slurp))

(defn score
  "Calculate the score from a line in the strategy guide for part 1."
  [line]
  (let [me (subs line 2)]
    (+ (case me
         "X" 1
         "Y" 2
         "Z" 3)
       (case line
         "A X" 3
         "A Y" 6
         "A Z" 0
         "B X" 0
         "B Y" 3
         "B Z" 6
         "C X" 6
         "C Y" 0
         "C Z" 3))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (->> data
        str/split-lines
        (map score)
        (apply +))))

(defn score-2
  "Calculate the score from a line in the strategy guide for part 2."
  [line]
  (case line
    "A X" 3
    "A Y" 4
    "A Z" 8
    "B X" 1
    "B Y" 5
    "B Z" 9
    "C X" 2
    "C Y" 6
    "C Z" 7))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (->> data
        str/split-lines
        (map score-2)
        (apply +))))

(def sample-data
  "A Y
B X
C Z")
