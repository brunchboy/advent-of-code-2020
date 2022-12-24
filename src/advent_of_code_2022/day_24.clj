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
  most one blizzard per cell. The blizzards are built as a lazy
  sequence of iterating the above step function, but since they repeat
  after the lowest common multiple of the height and width (which for
  the sample data and my puzzle data is just the product of the height
  and width), I can stop using the step function and just cycle the
  sequence at that point."
  [data]
  (let [grid   (vec (str/split-lines data))
        height (- (count grid) 2)
        width  (- (count (first grid)) 2)]
    {:height height
     :width  width
     :blizzards (->> (iterate (partial step height width)
                                        (->> (for [x (range 1 (inc width))
                                                   y (range 1 (inc height))]
                                               (let [cell (str (get-in grid [y x]))]
                                                 (when (not= "." cell)
                                                   [[x y] [cell]])))
                                             (filter identity)
                                             (into {})))
                               (take (* width height))
                               cycle)}))

(defn expand
  "Given a set of positions, add any positions which can be reached from
  them in a single step."
  [positions height width]
  (reduce (fn [acc [x y]]
            (set/union acc
                       (->> (for [[dx dy] [[-1 0] [1 0] [0 -1] [0 1]]]
                              (let [x (+ x dx)
                                    y (+ y dy)]
                                (when (and (<= 1 x width)
                                           (<= 1 y height))
                                  [x y])))
                            (filter identity)
                            set)))
          positions
          positions))

(defn solve
  "Given a starting and ending point and the current blizzard state,
  return the number of steps required to get to the ending point."
  [state start end]
   ;; We find the set of all positions we can be in after each step, by
   ;; considering the places we can move from each previous possible position,
   ;; and removing those which are occupied by blizzards at that step.
   ;; As soon as one of the positions is adjacent to the exit, we have found
   ;; the solution.
  (let [{:keys [height width blizzards]} state]
    (loop [steps     0
           positions #{start}
           blizzards blizzards]
      #_(println steps positions end)
      (if (positions end)
        (inc steps)  ; We have reached the exit!
        (recur (inc steps)
               (set/difference (expand positions height width) (set (keys (second blizzards))))
               (rest blizzards))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [{:keys [height width] :as state} (read-input data)]
     (solve state [1 0] [width height]))))

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
   (let [{:keys [height width] :as state} (read-input data)
         run-1 (solve state [1 0] [width height])
         run-2 (solve (update state :blizzards #(drop run-1 %)) [width (inc height)] [1 1])
         run-3 (solve (update state :blizzards #(drop (+ run-1 run-2) %)) [1 0] [width height])]
     [run-1 run-2 run-3 (+ run-1 run-2 run-3)])))
