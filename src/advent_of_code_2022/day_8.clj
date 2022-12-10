(ns advent-of-code-2022.day-8
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_8.txt")
       slurp))

(defn parse-input
  "Processes the input lines of the puzzle statement, building a vector
  of vectors of tree heights."
  [lines]
  (mapv (fn [line] (mapv #(Long/parseLong (str %)) line)) lines))

(defn height
  "Returns the height of the tree at the specified coordinate."
  [trees x y]
  (get-in trees [y x]))

(defn move
  "Returns the result of moving one step along a compass direction from a
  given coordinate."
  [x y direction]
  (case direction
    :north [x (dec y)]
    :east  [(inc x) y]
    :south [x (inc y)]
    :west  [(dec x) y]))

(defn visible?
  "Checks whether the tree at the specified coordinates is visible from
  outside the forest, according to the rules of the problem. If a
  direction is specified, considers only visibility from that
  direction."
  ([trees x y]
   (first (filter identity (for [direction [:north :east :south :west]]
                             (visible? trees x y (height trees x y) direction)))))
  ([trees x y our-height direction]
   (or (zero? x)
       (zero? y)
       (>= x (dec (count (first trees))))
       (>= y (dec (count trees)))
       (let [[new-x new-y] (move x y direction)]
         (and (< (height trees new-x new-y) our-height)
              (visible? trees new-x new-y our-height direction))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [trees (parse-input (str/split-lines data))]
     (->> (for [x (range (count (first trees)))
                y (range (count trees))]
            (visible? trees x y))
          (filter identity)
          count))))

(defn score
  "Calculate the scenic score for a tree."
  ([trees x y]
   (apply * (for [direction [:north :east :south :west]]
              (score trees x y (height trees x y) direction))))
  ([trees x y our-height direction]
   (let [[new-x new-y] (move x y direction)]
     (case direction
       :north (cond (zero? y)
                    0

                    (>= (height trees new-x new-y) our-height)
                    1

                    :else
                    (inc (score trees new-x new-y our-height direction)))

       :east (cond (>= new-x (count (first trees)))
                   0

                   (>= (height trees new-x new-y) our-height)
                   1

                   :else
                   (inc (score trees new-x new-y our-height direction)))

       :south (cond (>= new-y (count trees))
                    0

                    (>= (height trees new-x new-y) our-height)
                    1

                    :else
                    (inc (score trees new-x new-y our-height direction)))

       :west (cond (zero? x)
                   0

                   (>= (height trees new-x new-y) our-height)
                   1

                   :else
                   (inc (score trees new-x new-y our-height direction)))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [trees (parse-input (str/split-lines data))]
     (->> (for [x (range (count (first trees)))
                y (range (count trees))]
            (score trees x y))
          (apply max)))))

(def sample-input
  "30373
25512
65332
33549
35390")
