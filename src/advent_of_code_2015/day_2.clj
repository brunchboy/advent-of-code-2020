(ns advent-of-code-2015.day-2
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn read-dimensions
  "Reads the dimensions of a single present, parsing them into numbers."
  [line]
  (mapv #(Long/parseLong %) (str/split line #"x")))

(def input
  "The list of present dimensions (the puzzle input)."
  (->> (io/resource "2015/day_2.txt")
       io/reader
       line-seq
       (map read-dimensions)))

(defn paper-needed
  "Calculates the paper needed to wrap a present with the specified
  dimensions."
  [[l w h]]
  (let [sides [(* l w) (* w h) (* h l)]]
    (+ (* 2 (apply + sides)) (apply min sides))))

(defn part-1
  "Solve part 1."
  []
  (apply + (map paper-needed input)))

(defn ribbon-needed
  "Calculates the ribbon needed to wrap a present with the specified
  dimensions."
  [sides]
  (+ (* 2 (apply + (take 2 (sort sides))))
     (apply * sides)))

(defn part-2
  "Solve part 2."
  []
  (apply + (map ribbon-needed input)))
