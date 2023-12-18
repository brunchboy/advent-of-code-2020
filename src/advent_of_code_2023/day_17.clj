(ns advent-of-code-2023.day-17
  "Solutions for day 17."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def sample-input
  "The sample data."
  (->> "R 6 (#70c710)
D 5 (#0dc571)
L 2 (#5713f0)
D 2 (#d2c081)
R 2 (#59c680)
D 2 (#411b91)
L 5 (#8ceee2)
U 2 (#caa173)
L 1 (#1b58a2)
U 2 (#caa171)
R 2 (#7807d2)
U 3 (#a77fa3)
L 2 (#015232)
U 2 (#7a21e3)"
       str/split-lines))

(def input
  "The actual puzzle data."
  (->> (io/resource "2023/day_17.txt")
      slurp
      str/split-lines))

(defn move
  [x y direction distance]
  (case direction
    "R" [(+ x distance) y]
    "L" [(- x distance) y]
    "U" [x (- y distance)]
    "D" [x (+ y distance)]))

(defn move-digger
  [x y direction distance color]
  (let [dug (reduce (fn [acc i]
                      (assoc acc (move x y direction i) color))
                    {}
                    (range (inc distance)))]
    (concat (move x y direction distance) [dug])))

(defn dig-trench
  [data]
  (loop [left   data
         x      0
         y      0
         result {}]
    (if (empty? left)
      result
      (let [[_ direction distance color] (re-matches #"(\w) (\d+) \((.+)\)" (first left))
            [x y dug]                    (move-digger x y direction (parse-long distance) color)]
        (recur (rest left)
               x
               y
               (merge result dug))))))

(defn draw-trench
  [squares flood-cell]
  (let [cells (keys squares)
        min-x (apply min (map first cells))
        min-y (apply min (map second cells))
        max-x (apply max (map first cells))
        max-y (apply max (map second cells))]
    (doseq [y (range min-y (inc max-y))]
      (doseq [x (range min-x (inc max-x))]
        (print (if (get squares [x y]) "#" (if (= flood-cell [x y]) "*" "."))))
      (println))))

(defn find-flood-fill-spot
  [squares]
  (let [cells     (keys squares)
        min-x     (apply min (map first cells))
        min-y     (apply min (map second cells))
        max-x     (apply max (map first cells))
        first-dug (first (filter squares (for [x (range min-x (inc max-x))]
                                           [x min-y])))]
    [(inc (first first-dug)) (inc min-y)]))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [trench (dig-trench data)]
     (draw-trench trench (find-flood-fill-spot trench)))))
