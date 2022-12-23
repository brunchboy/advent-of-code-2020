(ns advent-of-code-2022.day-22
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_22.txt")
       slurp))

(defn read-input
  "Reads the input, returning the grid as a vector of strings, the
  instructions as a list of either numbers (a distance to move) or
  strings (a direction to turn), and the starting position and facing."
  [data]
  (let [[grid _ moves] (->> data
                            str/split-lines
                            (partition-by str/blank?))]
    {:grid   (vec grid)
     :moves  (->> moves
                  first
                  (partition-by #(Character/isDigit %))
                  (map #(if (Character/isDigit (first %))
                          (Long/parseLong (apply str %))
                          (apply str %))))
     :facing 0
     :y      1
     :x (->> (interleave (first grid) (drop 1 (range)))
             (partition 2)
             (drop-while (fn [[cell _x]] (not= cell \.)))
             first
             second)}))

(defn cell-at
  "Get the cell at a specific grid position, or for part 2, a location on
  a face."
  ([grid x y]
   (get-in grid [(dec y) (dec x)]))
  ([faces face x y]
   (get-in faces [face (dec y) (dec x)])))

(defn off-map?
  "Check whether the specified location is in a part of the map that
  represents an actual space you can potentially move."
  [grid x y]
  (or (> y (count grid))
      (< y 1)
      (> x (count (get grid (dec y))))
      (< x 1 )
      (= \space (cell-at grid x y))))

(defn wrapping-step
  "Try to move a step in a particular direction. If that takes you off
  the map, wraps you to the other side of the map. Does not consider
  obstacles, just returns the coordinates to check for them."
  [x y facing grid]
  (case facing
    0 (let [x (inc x)]
        (if (off-map? grid x y)
          (loop [x 1]
            (if (= \space (cell-at grid x y))
              (recur (inc x))
              [x y]))
          [x y]))
    1 (let [y (inc y)]
        (if (off-map? grid x y)
          (loop [y 1]
            (if (off-map? grid x y)
              (recur (inc y))
              [x y]))
          [x y]))
    2 (let [x (dec x)]
        (if (off-map? grid x y)
          (loop [x (count (get grid (dec y)))]
            (if (= \space (cell-at grid x y))
              (recur (dec x))
              [x y]))
          [x y]))
    3 (let [y (dec y)]
        (if (off-map? grid x y)
          (loop [y (count grid)]
            (if (off-map? grid x y)
              (recur (dec y))
              [x y]))
          [x y]))))

(defn move
  "Move a particular distance at our current facing on the map.
  Handle wrapping off ends, and stopping if we reach an obstacle."
  [{:keys [facing grid] :as state} distance]
  (loop [distance distance
         state    state]
    (if (pos? distance)
      (let [[next-x next-y] (wrapping-step (:x state) (:y state) facing grid)]
        (if (= \# (cell-at grid next-x next-y))
          state
          (recur (dec distance)
                 (assoc state :x next-x :y next-y))))
      state)))

(defn turn
  "Changes our facing to represent a turn in the specified direction."
  [state direction]
  (update state :facing #(mod (+ % (case direction "R" 1 "L" -1)) 4)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [start                (read-input data)
         solved               (reduce (fn [state instruction]
                                        (if (number? instruction)
                                          (move state instruction)
                                          (turn state instruction)))
                                      start
                                      (:moves start))
         {:keys [x y facing]} solved]
     (+ (* 1000 y) (* 4 x) facing))))

(defn extract-face
  "Pull a face of the cube from the larger grid, given side of each side,
  and the number of faces to skip in the x and y directions."
  [grid side x y]
  (->> (drop (* side y) grid)
       (take side)
       (map #(drop (* side x) %))
       (map #(take side %))
       (mapv #(apply str %))))

(defn cubify-sample
  "Convert the part 1 grid into a cube for part 2. Sadly, this only works
  for the way my sample map folds up."
  [{:keys [grid] :as state}]
  (let [side (quot (count (first grid)) 3)]
    ;; I am sure I could do this in a more elegant way, but quick and
    ;; dirty is good enough.
    (assoc state
           :side side
           :faces {1 (extract-face grid side 2 0)
                   2 (extract-face grid side 0 1)
                   3 (extract-face grid side 1 1)
                   4 (extract-face grid side 2 1)
                   5 (extract-face grid side 2 2)
                   6 (extract-face grid side 3 2)}
           :face 1
           :x (- (:x state) (* 2 side)))))


(defn wrapping-step-3d-sample
  "Try to move a step in a particular direction on the cube formed by the
  sample problem. If that takes you off the map, wraps you to the
  proper edge of the adjoining face, and updates your facing
  appropriately. Does not consider obstacles, just returns the
  coordinates to check for them. Returns a tuple of your new face, x,
  y, and facing."
  [face x y facing side]
  (case facing
    0 (let [x (inc x)]
        (if (<= x side)
          [face x y facing]
          (case face
            1 [6 side (- (inc side) y) 2]
            2 [3 1 y 0]
            3 [4 1 y 0]
            4 [6 (- (inc side) y) 1 1]
            5 [6 1 y 0]
            6 [1 side (- (inc side) y) 2])))
    1 (let [y (inc y)]
        (if (<= y side)
          [face x y facing]
          (case face
            1 [4 x 1 1]
            2 [5 (- (inc side) x) side 3]
            3 [5 1 (- (inc side) x) 0]
            4 [5 x 1 1]
            5 [2 (- (inc side) x) side 3]
            6 [2 1 (- (inc side) x) 0])))
    2 (let [x (dec x)]
        (if (pos? x)
          [face x y facing]
          (case face
            1 [3 y 1 1]
            2 [6 (- (inc side) y) side 3]
            3 [2 side y 2]
            4 [3 side y 2]
            5 [3 (- (inc side) y) side 3]
            6 [5 side y 2])))
    3 (let [y (dec y)]
        (if (pos? y)
          [face x y facing]
          (case face
            1 [2 (- (inc side) x) 1 1]
            2 [1 (- (inc side) x) 1 1]
            3 [1 1 x 0]
            4 [1 x side 3]
            5 [4 x side 3]
            6 [4 side (- (inc side) x) 2])))))

(defn move-3d
  "Move a particular distance at our current facing on the cube
  represented by part 2. Handle wrapping from face to face (using
  `step-fn` to implement the details of how a particular map is
  folded), and stopping if we reach an obstacle."
  [{:keys [side faces] :as state} distance step-fn]
  (loop [distance distance
         state    state]
    (if (pos? distance)
      (let [{:keys [face x y facing]} state
            [next-face next-x next-y next-facing] (step-fn face x y facing side)]
        (if (= \# (cell-at faces next-face next-x next-y))
          state
          (recur (dec distance)
                 (assoc state :face next-face :x next-x :y next-y :facing next-facing))))
      state)))

(defn flatten-x-sample
  "Converts an x coordinate on a face back to its coordinate on the
  flat grid. This version only works for the sample data map."
  [x face side]
  (+ x (* side (case face
                 1 2
                 2 0
                 3 1
                 4 2
                 5 2
                 6 3))))

(defn flatten-y-sample
  "Converts a y coordinate on a face back to its coordinate on the
  flat grid. This version only works for the sample data map."
  [y face side]
  (+ y (* side (case face
                 1 0
                 2 1
                 3 1
                 4 1
                 5 2
                 6 2))))

(defn print-faces
  "Debugging helper to see how my faces are being built. This is how I
  realized that the sample map folded differently than the actual
  puzzle map, grr! I should have anticipated such trickery."
  [state]
  (doseq [face (map inc (range 6))]
    (println "Face" face)
    (println (apply str " " (map #(mod (inc %) 10) (range (:side state)))))
    (doseq [y (range (:side state))]
      (println (str (mod (inc y) 10) (get-in state [:faces face y]))))
    (println)))

(def sample-input
  "The test data."
  "        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5")

(defn part-2-sample
  "Solve the part 2 sample data."
  []
  (let [start  (cubify-sample (read-input sample-input))
        _      (print-faces start)
        solved (reduce (fn [state instruction]
                         (println (:face state) (:x state) (:y state) (:facing state) instruction)
                         (if (number? instruction)
                           (move-3d state instruction wrapping-step-3d-sample)
                           (turn state instruction)))
                       start
                       (:moves start))

        {:keys [face x y facing side]} solved]
    (+ (* 1000 (flatten-y-sample y face side))
       (* 4 (flatten-x-sample x face side))
       facing)))

(defn cubify
  "Convert the part 1 grid into a cube for part 2, given the way my
  actual puzzle map folds up."
  [{:keys [grid] :as state}]
  (let [side (quot (count (first grid)) 3)]
    ;; I am sure I could do this in a more elegant way, but quick and
    ;; dirty is good enough.
    (assoc state
           :side side
           :faces {1 (extract-face grid side 1 0)
                   2 (extract-face grid side 2 0)
                   3 (extract-face grid side 1 1)
                   4 (extract-face grid side 0 2)
                   5 (extract-face grid side 1 2)
                   6 (extract-face grid side 0 3)}
           :face 1
           :x (- (:x state) side))))

(defn wrapping-step-3d
  "Try to move a step in a particular direction on the cube formed by my
  actual puzzle data. If that takes you off the map, wraps you to the
  proper edge of the adjoining face, and updates your facing
  appropriately. Does not consider obstacles, just returns the
  coordinates to check for them. Returns a tuple of your new face, x,
  y, and facing."  [face x y facing side]
  (case facing
    0 (let [x (inc x)]
        (if (<= x side)
          [face x y facing]
          (case face
            1 [2 1 y 0]
            2 [5 side (- (inc side) y) 2]
            3 [2 y side 3]
            4 [5 1 y 0]
            5 [2 side (- (inc side) y) 2]
            6 [5 y side 3])))
    1 (let [y (inc y)]
        (if (<= y side)
          [face x y facing]
          (case face
            1 [3 x 1 1]
            2 [3 side x 2]
            3 [5 x 1 1]
            4 [6 x 1 1]
            5 [6 side x 2]
            6 [2 x 1 1])))
    2 (let [x (dec x)]
        (if (pos? x)
          [face x y facing]
          (case face
            1 [4 1 (- (inc side) y) 0]
            2 [1 side y 2]
            3 [4 y 1 1]
            4 [1 1 (- (inc side) y) 0]
            5 [4 side y 2]
            6 [1 y 1 1])))
    3 (let [y (dec y)]
        (if (pos? y)
          [face x y facing]
          (case face
            1 [6 1 x 0]
            2 [6 x side 3]
            3 [1 x side 3]
            4 [3 1 x 0]
            5 [3 x side 3]
            6 [4 x side 3])))))

(defn flatten-x
  "Converts an x coordinate on a face back to its coordinate on the
  flat grid, based on how my actual puzzle map folds up."
  [x face side]
  (+ x (* side (case face
                 1 1
                 2 2
                 3 1
                 4 0
                 5 1
                 6 0))))

(defn flatten-y
  "Converts a y coordinate on a face back to its coordinate on the
  flat grid, based on how my actual puzzle map folds up."
  [y face side]
  (+ y (* side (case face
                 1 0
                 2 0
                 3 1
                 4 2
                 5 2
                 6 3))))

(defn part-2
  "Solve part 2."
  []
  (let [start  (cubify (read-input input))
        solved (reduce (fn [state instruction]
                         (if (number? instruction)
                           (move-3d state instruction wrapping-step-3d)
                           (turn state instruction)))
                       start
                       (:moves start))

        {:keys [face x y facing side]} solved]
    (+ (* 1000 (flatten-y y face side))
       (* 4 (flatten-x x face side))
       facing)))
