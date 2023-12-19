(ns advent-of-code-2023.day-18
  "Solutions for day 18."
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
  (->> (io/resource "2023/day_18.txt")
      slurp
      str/split-lines))

(defn move
  "Return the result of moving from a starting point in a given direction
  a specified distance."
  [x y direction distance]
  (case direction
    "R" [(+ x distance) y]
    "L" [(- x distance) y]
    "U" [x (- y distance)]
    "D" [x (+ y distance)]))

(defn move-digger
  "Calculate the results of moving the digger from a starting point in a
  given direction a specified distance. Returns the destination point,
  as well as a map from all the points traversed to the colors assigned
  to those points."
  [x y direction distance color]
  (let [dug (reduce (fn [acc i]
                      (assoc acc (move x y direction i) color))
                    {}
                    (range (inc distance)))]
    (concat (move x y direction distance) [dug])))

(defn dig-trench
  "Returns the set of points colored by the digger as it follows the
  instructions in the puzzle input."
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
  "As a debugging aid, draw the result returned by dig-trench, in the
  same format used in the problem statement."
  ([squares]
   (draw-trench squares nil))
  ([squares flood-cell]
   (let [cells (keys squares)
         min-x (apply min (map first cells))
         min-y (apply min (map second cells))
         max-x (apply max (map first cells))
         max-y (apply max (map second cells))]
     (doseq [y (range min-y (inc max-y))]
       (doseq [x (range min-x (inc max-x))]
         (print (if (get squares [x y]) "#" (if (= flood-cell [x y]) "*" "."))))
       (println)))))

(defn find-flood-fill-spot
  "Find a place from which we can flood-fill the area inside the trench
  that was dug. I found that scanning along the top row until a trench
  cell was found and moving one pixel right and down worked (ended up
  inside the trench boundaries) with both the sample input and my
  actual puzzle input, but this is not guaranteed to be a general
  solution."
  [squares]
  (let [cells     (keys squares)
        min-x     (apply min (map first cells))
        min-y     (apply min (map second cells))
        max-x     (apply max (map first cells))
        first-dug (first (filter squares (for [x (range min-x (inc max-x))]
                                           [x min-y])))]
    [(inc (first first-dug)) (inc min-y)]))

(defn flood-fill
  "Fill in all the empty cells from a starting point, stopping when you
  reach any non-empty cells. A simple recursive approach."
  [squares flood-cell]
  (if (get squares flood-cell)
    squares
    (let [[x y] flood-cell]
      (-> squares
          (assoc flood-cell "filled")
          (flood-fill [(dec x) y])
          (flood-fill [(inc x) y])
          (flood-fill [x (dec y)])
          (flood-fill [x (inc y)])))))

(defn part-1
  "Solve part 1 using the flood-fill approach."
  ([]
   (part-1 input))
  ([data]
   (let [trench (dig-trench data)]
     (count (flood-fill trench (find-flood-fill-spot trench))))))

(defn gather-vertices
  "Find all the vertices that make up the polygon of the trench boundary,
  since flood filling will be far too inefficient for part 2."
  [data]
  (loop [left   data
         x      0
         y      0
         result [[0 0]]]
    (if (empty? left)
      result
      (let [[_ direction distance _color] (re-matches #"(\w) (\d+) \((.+)\)" (first left))
            [x y]                    (move x y direction (parse-long distance))]
        (recur (rest left)
               x
               y
               (conj result [x y]))))))

(defn total-distance
  "Calculate the perimiter of the trench boundary, by simply adding up
  the distances moved by the digger."
  [data]
  (loop [left   data
         result 0]
    (if (empty? left)
      result
      (let [[_ _ distance] (re-matches #"(\w) (\d+) \((.+)\)" (first left))]
        (recur (rest left)
               (+ result (parse-long distance)))))))

(defn area
  "Calculate the area of the polygon represented by the trench"
  [vertices]
  (quot (abs (- (reduce + (map (fn [[[x-1 _] [_ y-2]]] (* x-1 y-2))
                               (partition 2 1 vertices)))
                (reduce + (map (fn [[[_ y-1] [x-2 _]]] (* y-1 x-2))
                               (partition 2 1 vertices)))))
     2))

(defn part-1-b
  "Solve part 1 using polygon formula, adding half of the perimiter,
  since our lines are actually one cell wide, not geometric
  abstractions. This is done as a sanity check before tackling part 2."
  ([]
   (part-1-b input))
  ([data]
   (let [points   (area (gather-vertices data))
         boundary (total-distance data)]
     (println points boundary)
     (+ points 1 (/ boundary 2)))))

(defn gather-vertices-2
  "Find the vertices that make up the polygon dug when following the
  instructions as specified in part 2 of the problem."
  [data]
  (loop [left   data
         x      0
         y      0
         result [[0 0]]]
    (if (empty? left)
      result
      (let [[_ hex]   (re-matches #"\w \d+ \(#(.+)\)" (first left))
            distance  (Long/parseLong (subs hex 0 5) 16)
            direction (case (subs hex 5)
                        "0" "R"
                        "1" "D"
                        "2" "L"
                        "3" "U")
            [x y]     (move x y direction distance)]
        (recur (rest left)
               x
               y
               (conj result [x y]))))))

(defn total-distance-2
  "Find the perimiter of the polygon dug when following the
  instructions as specified in part 2 of the problem."
  [data]
  (loop [left   data
         result 0]
    (if (empty? left)
      result
      (let [[_ hex] (re-matches #"\w \d+ \(#(.+)\)" (first left))
            distance (Long/parseLong (subs hex 0 5) 16)]
        (recur (rest left)
               (+ result distance))))))


(defn part-2
  "Solve part 2 using the polygon formula and revised parsing rules."
  ([]
   (part-2 input))
  ([data]
   (let [points   (area (gather-vertices-2 data))
         boundary (total-distance-2 data)]
     (println points boundary)
     (+ points 1 (/ boundary 2)))))
