(ns advent-of-code-2023.day-13
  "Solution for day 13."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def sample-input
  "The sample puzzle input."
  "#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#")

(def input
  "The puzzle input."
  (->> (io/resource "2023/day_13.txt")
       slurp))

(defn read-patterns
  "Read a sequence of pattern grids from the puzzle input."
  [data]
  (->> data
       str/split-lines
       (partition-by count)
       (take-nth 2)
       (map vec)))

(defn transpose-pattern
  "Rotates the pattern 90 degrees."
  [pattern]
  (vec (apply map str pattern)))

(defn reflects-below?
  "Given a pattern and a row number, returns that row number if the
  pattern below it is a perfect reflection of above."
  [pattern row]
  (loop [offset 0]
    (let [upper (+ row offset)
          lower (- row offset 1)]
      (if (not= (get pattern upper)
                (get pattern lower))
        nil
        (if (or (zero? lower)
                (= upper (dec (count pattern))))
          row  ; We have found perfect reflections to the edge of the pattern, success.
          (recur (inc offset)))))))

(defn find-mirror
  "Finds the row below which the pattern perfectly reflects the rows
  above, if any."
  [pattern]
  (some (partial reflects-below? pattern) (range 1 (count pattern))))

(defn score
  "Find the row or column in which the pattern in reflected, and return
  an appropriate score for the pattern given the problem rules."
  [pattern]
  (if-let [row (find-mirror pattern)]
    (* 100 row)
    (find-mirror (transpose-pattern pattern))))

(defn part-1
  "Solve part 1"
  ([]
   (part-1 input))
  ([data]
   (->> data
        read-patterns
        (map score)
        (reduce +))))

(defn toggle
  "Swaps the value of a pattern at the given row and column."
  [pattern row column]
  (update pattern row
          (fn [s]
            (apply str (update (vec s) column
                               (fn [c]
                                 (if (= c \#) \. \#)))))))

(defn desmudged-score
  [pattern]
  (let [old-score (score pattern)]
    (->> (for [row    (range (count pattern))
               column (range (count (first pattern)))]
           (toggle pattern row column))
         (map score)
         (filter (fn [score] (and score (not= score old-score))))
         first)))

(defn part-2
  "Solve part 2"
  ([]
   (part-2 input))
  ([data]
   (->> data
        read-patterns
        (map desmudged-score)
        #_(reduce +))))
