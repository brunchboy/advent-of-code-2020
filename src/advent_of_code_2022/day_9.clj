(ns advent-of-code-2022.day-9
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_9.txt")
       slurp))

;; The rope state is represented as a pair of tuples, the coordinates
;; of the head followed by the coordinates of the tail. I also keep
;; track of a set of the coordinates visited by the tail.

(defn move
  "Determine the new position of the rope head given a direction
  indicator."
  [x y direction]
  (case direction
    "D" [x (dec y)]
    "R" [(inc x) y]
    "U" [x (inc y)]
    "L" [(dec x) y]))

(defn follow
  "Returns the new tail position given a moved head."
  [[head-x head-y] [tail-x tail-y]]
  (let [x-distance (Math/abs (- head-x tail-x))
        x-move     (if (> head-x tail-x) 1 -1)
        y-distance (Math/abs (- head-y tail-y))
        y-move     (if (> head-y tail-y) 1 -1)]
    (if (zero? x-distance)
      (if (zero? y-distance)
        [tail-x tail-y]
        (if (> y-distance 1)
          [tail-x (+ tail-y y-move)]
          [tail-x tail-y]))
      (if (zero? y-distance)
        (if (> x-distance 1)
          [(+ tail-x x-move) tail-y]
          [tail-x tail-y])
        (if (or (> x-distance 1) (> y-distance 1))
          [(+ tail-x x-move) (+ tail-y y-move)]
          [tail-x tail-y])))))

(defn step
  "Move the head of the rope in a direction, figure out where that leaves
  the tail, and update the set of places the tail has visited."
  [direction [[head-x head-y] [tail-x tail-y] visited]]
  (let [[head-x head-y] (move head-x head-y direction)
        [tail-x tail-y] (follow [head-x head-y] [tail-x tail-y])]
    [[head-x head-y] [tail-x tail-y] (conj visited [tail-x tail-y])]))

(defn apply-move-line
  "Given rope and visited states, apply a move instruction line and
  return the new state."
  [state line]
  (let [[direction distance] (str/split line #"\s+")
        distance             (Long/parseLong distance)]
    (nth (iterate (partial step direction) state) distance)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [[_ _ visited] (reduce apply-move-line [[0 0] [0 0] #{[0 0]}] (str/split-lines data))]
     (count visited))))

;; For part 2 the rope is represented by a tuple of ten coordinate
;; pairs, from the head to the tail.

(defn follow-2
  "Extended, recursive version of knot-following for part 2."
  [[head-x head-y] [[x y] & knots]]
  (let [[x y] (follow [head-x head-y] [x y])]
    (if (empty? knots)
      [[head-x head-y] [x y]]
      (concat [[head-x head-y]] (follow-2 [x y] knots)))))

(defn step-2
  "Move the head of the rope in a direction, figure out where that leaves
all knots including the tail, and update the set of places the tail has visited."
  [direction [rope visited]]
  (let [[[head-x head-y] & knots] rope
        [head-x head-y]           (move head-x head-y direction)
        rope                      (follow-2 [head-x head-y] knots)
        [tail-x tail-y]           (last rope)]
    [rope (conj visited [tail-x tail-y])]))

(defn apply-move-line-2
  "Given rope and visited states, apply a move instruction line and
  return the new state."
  [state line]
  (let [[direction distance] (str/split line #"\s+")
        distance             (Long/parseLong distance)]
    (nth (iterate (partial step-2 direction) state) distance)))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [[_rope visited] (reduce apply-move-line-2 [(repeat 10 [0 0]) #{[0 0]}] (str/split-lines data))]
     (count visited))))

(def sample-input
  "The puzzle sample input."
  "R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2")

(def sample-input-2
  "Larger sample input for part 2."
  "R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20")
