(ns advent-of-code-2022.day-24
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_24.txt")
       slurp))

(defn move
  "Calculates the next location of a blizzard moving a particular
  direction, taking into account wrapping at the edges of the valley."
  [x y direction height width]
  (case direction
    "<" (let [x (dec x)] (if (zero? x) [width y] [x y]))
    "^" (let [y (dec y)] (if (zero? y) [x height] [x y]))
    ">" (let [x (inc x)] (if (> x width) [1 y] [x y]))
    "v" (let [y (inc y)] (if (> y height) [x 1] [x y]))))

(defn step
  "Advances the blizzards one step in time."
  [height width blizzards]
  (reduce (fn [acc [[x y] directions]]
            (reduce (fn [acc direction]
                      (update acc (move x y direction height width) (fnil conj []) direction))
                    acc
                    directions))
          {}
          blizzards))

(defn read-input
  "Reads the input, determining the size of the valley and the position
  and directions of all the blizzards, as a map from coordinates to
  the list of directions with which any blizzards are passing through
  that coordinate. In the initial input data, we know there will be at
  most one blizzard per cell."
  [data]
  (let [grid   (vec (str/split-lines data))
        height (- (count grid) 2)
        width  (- (count (first grid)) 2)]
    {:height height
     :width  width
     :seen   #{}
     :queue  [{:position  [1 0]
               :steps     0
               :blizzards (->> (iterate (partial step height width)
                                        (->> (for [x (range 1 (inc width))
                                                   y (range 1 (inc height))]
                                               (let [cell (str (get-in grid [y x]))]
                                                 (when (not= "." cell)
                                                   [[x y] [cell]])))
                                             (filter identity)
                                             (into {})))
                               (take (* width height))
                               cycle)}]}))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (loop [{:keys [queue seen height width] :as state} (read-input data)]
     (let [{:keys [position steps blizzards] :as entry} (first queue)
           [x y]                                        position]
       (spit "/tmp/spit.txt" (str "x=" x ", y=" y ", steps="(:steps entry) ", seen=" (count seen)
                                  ", queue=" (count queue)))
       (if (and (= x width) (= y height))
         (inc steps) ; We have reached the exit!
         (recur (-> state
                    (update :seen conj [x y (mod steps (* width height))])
                    (assoc :queue
                           (concat (rest queue)
                                   (filter identity
                                           (for [[dx dy] [[-1 0] [0 0] [1 0] [0 -1] [0 1]]]
                                             (let [x (+ x dx)
                                                   y (+ y dy)]
                                               (when (and (not (seen [x y (mod steps
                                                                               (* width height))]))
                                                          (not ((second blizzards) [x y]))
                                                          (or (= [1 0] [x y]) ; Still at start
                                                              (and (<= 1 x width)  ; Within valley
                                                                   (<= 1 y height))))
                                                 (assoc entry :position [x y]
                                                        :steps (inc steps)
                                                        :blizzards (rest blizzards)))))))))))))))

(def sample-input
  "The test data."
  "#.######
#>>.<^<#
#.<..<<#
#>v.><>#
#<^v^^>#
######.#")

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   ))
