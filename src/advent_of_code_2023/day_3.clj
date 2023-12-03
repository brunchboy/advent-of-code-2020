(ns advent-of-code-2023.day-3
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2023/day_3.txt")
       slurp
       str/split-lines))

(def sample-input
  "The sample data for testing a solution."
  (->> "467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...$.*....
.664.598.."
       str/split-lines))

(defn char-at
  "Look up a character within the puzzle data returning '.' if out of
  bounds."
  [data row col]
  (get-in data [row col] \.))

(defn find-numbers
  "Find all numbers that are adjacent to at least one symbol."
  [data]
  (->> (for [row (range (count data))
             col (range (count (first data)))]
         (when (and (Character/isDigit (char-at data row col))
                    (not (Character/isDigit (char-at data row (dec col)))))
           (let [len (loop [len 1]
                       (if (Character/isDigit (char-at data row (+ col len)))
                         (recur (inc len))
                         len))]
             (when (some #(and (not= % \.)
                               (not (Character/isDigit %)))
                         (for [row (range (dec row) (+ row 2))
                               col (range (dec col) (+ col len 1))]
                           (char-at data row col)))
               (parse-long (subs (get data row) col (+ col len)))))))
       (filter identity)))

(defn part-1
  "Solve part 1"
  ([]
   (part-1 input))
  ([data]
   (reduce + (find-numbers data))))

(defn build-number-map
  "Given a list of number locations, build a map that returns the tuple
  of [number row first-column] given a point on which the number falls."
  [number-list]
  (reduce (fn [acc [row col len n]]
            (apply merge acc
                   (for [i (range col (+ col len))]
                     {[row i] [n row col]})))
          {}
          number-list))

(defn find-numbers-2
  "Build a map that can look up which unique number, if any, can be found
  at a point in the puzzle data."
  [data]
  (->> (for [row (range (count data))
             col (range (count (first data)))]
         (when (and (Character/isDigit (char-at data row col))
                    (not (Character/isDigit (char-at data row (dec col)))))
           (let [len (loop [len 1]
                       (if (Character/isDigit (char-at data row (+ col len)))
                         (recur (inc len))
                         len))]
             (when (some #(and (not= % \.)
                               (not (Character/isDigit %)))
                         (for [row (range (dec row) (+ row 2))
                               col (range (dec col) (+ col len 1))]
                           (char-at data row col)))
               [row col len (parse-long (subs (get data row) col (+ col len)))]))))
       (filter identity)
       build-number-map))

(defn find-gear-ratios
  "Return the results of multiplying all pairs of numbers that are
  adjacent to the same '*' symbol in the puzzle data."
  [data]
  (let [numbers (find-numbers-2 data)]
    (->> (for [row (range (count data))
               col (range (count (first data)))]
           (when (= \* (char-at data row col))
             (->> (for [i (range -1 2)
                        j (range -1 2)]
                    (get numbers [(+ row i) (+ col j)] nil))
                  (filter identity)
                  set)))
         (filter identity)
         (filter #(= (count %) 2))
         (map #(reduce * (map first %))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (reduce + (find-gear-ratios data))))
