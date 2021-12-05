(ns advent-of-code-2021.day-5
  "Solutions for day 5."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn read-line-spec
  "Reads a line specification from the puzzle input, returning it as a
  tuple of [x1 y1 x2 y2]."
  [line]
  (let [[_ x1 y1 x2 y2] (re-find #"(\d+),(\d+)\s+->\s+(\d+),(\d+)" line)]
    (vec (map #(Long/parseLong %) [x1 y1 x2 y2]))))

(defn explode-non-diagonals
  "Given a line specification, if it is horizontal or vertical, returns
  the [x y] coordinates of all grid points it covers."
  [[x1 y1 x2 y2]]
  (cond
    (= x1 x2)
    (for [y (range (min y1 y2) (inc (max y1 y2)))]
      [x1 y])

    (= y1 y2)
    (for [x (range (min x1 x2) (inc (max x1 x2)))]
      [x y1])))

(defn count-bad-points
  "Determines how many grid points are covered by more than one
  horizontal or vertical line."
  [lines]
  (->> lines
       str/split-lines
       (map read-line-spec)
       (map explode-non-diagonals)
       (filter identity)
       (apply concat)
       frequencies
       vals
       (filter #(> % 1))
       count))

(defn part-1
  "Solve part 1."
  []
  (count-bad-points (slurp (io/resource "2021/day_5.txt"))))

(defn explode-all-lines
  "Given a line specification, returns the [x y] coordinates of all grid
  points it covers."
  [[x1 y1 x2 y2]]
  (cond
    (= x1 x2)
    (for [y (range (min y1 y2) (inc (max y1 y2)))]
      [x1 y])

    (= y1 y2)
    (for [x (range (min x1 x2) (inc (max x1 x2)))]
      [x y1])

    (< x1 x2)
    (for [x (range x1 (inc x2))]
      [x (+ y1 (* (- x x1) (if (< y1 y2) 1 -1)))])

    :else
    (for [x (range x2 (inc x1))]
      [x (+ y1 (* (- x x1) (if (< y1 y2) -1 1)))])))

(defn count-bad-points-2
  "Determines how many grid points are covered by more than one line."
  [lines]
  (->> lines
       str/split-lines
       (map read-line-spec)
       (map explode-all-lines)
       (apply concat)
       frequencies
       vals
       (filter #(> % 1))
       count))

(defn part-2
  "Solve part 2."
  []
  (count-bad-points-2 (slurp (io/resource "2021/day_5.txt"))))
