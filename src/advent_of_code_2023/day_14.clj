(ns advent-of-code-2023.day-14
  "Solution for day 14."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(defn gather-rocks
  "Returns a set of the coordinates at which each instance of the
specified character is found in the rows of input data."
  [rows rock]
  (->> (map-indexed (fn [y row]
                      (map-indexed (fn [x c]
                                     (when (= c rock) [x y]))
                                   row))
                    rows)
       (apply concat)
       (filter identity)
       set))

(defn read-rocks
  "Reads the input data into sets of coordinates of rounded and
  cube-shaped rocks."
  [data]
  (let [rows (str/split-lines data)]
    {:rounded (gather-rocks rows \O)
     :cube-shaped (gather-rocks rows \#)
     :width (count (first rows))
     :height (count rows)}))

(def sample-input
  "The example data."
  (-> "O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#...."
      read-rocks))

(def input
  "The actual puzzle data."
  (-> (io/resource "2023/day_14.txt")
      slurp
      read-rocks))

(defn move-rock
  "Advance a rounded rock a single step in the direction it is moving, if
  that falls within the bounds of the grid, and that space is free."
  [grid [x y] direction]
  (let [target (case direction
                 :east  [(inc x) y]
                 :west  [(dec x) y]
                 :north [x (dec y)]
                 :south [x (inc y)]) ]
    (if (or (some neg? target)
            (> (first target) (dec (:width grid)))
            (> (second target) (dec (:height grid)))
            (contains? (:rounded grid) target)
            (contains? (:cube-shaped grid) target))
      grid
      (-> grid
          (update :rounded disj [x y])
          (update :rounded conj target)))))

(defn roll
  "Move every rock which is free to do so one step in the direction
  specified."
  [rocks direction])

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   ))


(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
))
