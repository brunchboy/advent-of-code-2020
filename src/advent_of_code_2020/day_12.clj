(ns advent-of-code-2020.day-12
  "Solutions for day 12"
  (:require [clojure.java.io :as io]))

(def input
  "The navigation instructions (puzzle input)."
  (->> (io/resource "day_12.txt")
       io/reader
       line-seq))

(defn move
  "Implements a move in the direction specified by `facing`, where 0
  means east, 1 means south, 2 means west, and 3 means north."
  [[x y facing] direction distance]
  (case direction
    0 [(+ x distance) y facing]
    1 [x (- y distance) facing]
    2 [(- x distance) y facing]
    3 [x (+ y distance) facing]))

(defn rotate
  "Rotate the ship's by the specified number of degrees,
  updating the `facing` value described in `move`. Only right angles
  are supported."
  [[x y facing] degrees]
  [x y (mod (+ facing (quot degrees 90)) 4)])

(defn process-instruction
  "Given a ship position as a tuple of `[x y facing]`, and a movement
  instruction as defined in the problem statement, return the updated
  ship position that would result from following that instruction."
  [position instruction]
  (if-let [[_ action distance] (re-matches #"([A-Z])(\d+)" instruction)]
    (let [distance (Long/parseLong distance)]
      (case action
        "N" (move position 3 distance)
        "S" (move position 1 distance)
        "E" (move position 0 distance)
        "W" (move position 2 distance)
        "F" (move position (nth position 2) distance)
        "R" (rotate position distance)
        "L" (rotate position (- distance))))
    (throw (Exception. (str "Unrecognized instruction format: " instruction)))))

(defn part-1
  "Solve part 1 of the problem. If `instructions` are supplied, use them
  instead of `input`. Return the Manhattan distance of the ship from
  its starting point after following the instructions under the
  interpretation of part 1."
  ([]
   (part-1 input))
  ([instructions]
   (->> (reduce process-instruction [0 0 0] instructions)
        (map #(Math/abs %))
        (take 2)
        (apply +))))

(defn rotate-waypoint
  "Given a current waypoint definition, return the result of rotating it
  through the specified number of degrees. Only right angles are
  supported."
  [wx wy degrees]
  (let [normalized (mod (quot degrees 90) 4)]
    (case normalized
      0 [wx wy]
      1 [wy (- wx)]
      2 [(- wx) (- wy)]
      3 [(- wy) wx])))

(defn process-instruction-2
  "Given a ship state as a tuple of `[x y waypoint-x waypoint-y]`, and a
  movement instruction as redefined by the part 2 problem statement,
  return the updated ship state that would result from following that
  instruction."
  [[x y wx wy] instruction]
  (if-let [[_ action distance] (re-matches #"([A-Z])(\d+)" instruction)]
    (let [distance (Long/parseLong distance)]
      (case action
        "N" [x y wx (+ wy distance)]
        "S" [x y wx (- wy distance)]
        "E" [x y (+ wx distance) wy]
        "W" [x y (- wx distance) wy]
        "F" [(+ x (* wx distance)) (+ y (* wy distance)) wx wy]
        "R" (vec (concat [x y] (rotate-waypoint wx wy distance)))
        "L" (vec (concat [x y] (rotate-waypoint wx wy (- distance))))))
    (throw (Exception. (str "Unrecognized instruction format: " instruction)))))

(defn part-2
  "Solve part 1 of the problem. If `instructions` are supplied, use them
  instead of `input`. Return the Manhattan distance of the ship from
  its starting point after following the instructions under the
  interpretation of part 1."
  ([]
   (part-2 input))
  ([instructions]
   (->>
    (reduce process-instruction-2 [0 0 10 1] instructions)
    (map #(Math/abs %))
    (take 2)
    (apply +))))
