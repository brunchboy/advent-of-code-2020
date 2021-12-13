(ns advent-of-code-2021.day-13
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The puzzle input."
  (-> (io/resource "2021/day_13.txt")
      slurp))

(defn build-dot-set
  "Given a list of x,y coordinates, build a set representing all those
  points, as [x y] tuples."
  [lines]
  (reduce (fn [acc line]
            (let [[x y] (str/split line #",")]
              (conj acc [(Long/parseLong x) (Long/parseLong y)])))
          #{}
          lines))

(defn apply-fold
  "Calculate the points remaining in a point set after folding at the
  specified distance along the specified axis as described in the
  problem statement."
  [dots fold]
  (let [[_ axis n] (re-matches #"fold along (.)=(\d+)" fold)
        n          (Long/parseLong n)]
    (reduce (fn [acc dot]
              (let [[x y] dot]
                (conj acc (if (= axis "x")
                            (if (> x n)
                              [(- (* 2 n) x) y]
                              [x y])
                            (if (> y n)
                              [x (- (* 2 n) y)]
                              [x y])))))
            #{}
            dots)))

(defn bounds
  "Find the largest x and y coordinates in a dot set."
  [dots]
  (reduce (fn [acc dot]
            (let [[width height] acc
                  [x y]         dot]
              [(max x width) (max y height)]))
          [0 0]
          dots))

(defn print-dots
  "Print out the locations of visible dots in a dot set, so the problem
  solution can be read."
  [dots]
  (let [[width height] (bounds dots)]
    (doseq [y (range (inc height))]
      (doseq [x (range (inc width))]
        (print (if (dots [x y]) "#" " ")))
      (println))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [[dots _ folds] (->> data str/split-lines (partition-by str/blank?))
         dots (build-dot-set dots)]
     (print-dots (reduce apply-fold dots folds)))))
