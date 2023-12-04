(ns advent-of-code-2023.day-4
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.math.numeric-tower :as math]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2023/day_4.txt")
       slurp
       str/split-lines))

(def sample-input
  "The sample data for testing a solution."
  (->> "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
       str/split-lines))

(defn parse-set-of-numbers
  "Given a string of space-separated integers, parses them into a set."
  [s]
  (edn/read-string (str "#{" s "}")))

(defn count-card-matches
  "Determine how many winning numbers are on a scratch card."
  [card]
  (let [[_ v]          (str/split card #": ")
        [winners nums] (map parse-set-of-numbers (str/split v #"\|"))]
    (count (set/intersection winners nums))))

(defn score-card
  "Calculate the score of a scratch card using the rules of part 1."
  [card]
  (let [matches (count-card-matches card)]
    (if (zero? matches)
      0
      (math/expt 2 (dec matches)))))


(defn part-1
  "Solve part 1"
  ([]
   (part-1 input))
  ([data]
   (apply + (map score-card data))))

(def sample-input-2
  "The sample data for testing a solution for part 2."
  (->> "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"
       str/split-lines))

(defn update-counts
  "Given the current counts of each scratch card, the index of the one
  being played, and how many new scratch cards have been won by this
  card, updates the counts."
  [counts i won]
  (let [copies (get counts i)]  ; We win one for each copy of winning card.
    (loop [result []
           j      0]
      (if (< j (count counts))
        (recur (conj result (+ (get counts j)
                               (if (< i j (+ i won 1))
                                 copies
                                 0)))
               (inc j))
        result))))

(defn part-2
  "Solve part 2"
  ([]
   (part-2 input))
  ([data]
   (loop [counts (vec (repeat (count data) 1))
          i      0]
     (if (< i (count data))
       (let [won (count-card-matches (get data i))]
         (recur (update-counts counts i won)
                (inc i)))
       (reduce + counts)))))
