(ns advent-of-code-2017.day-11
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The path taken by the child process (puzzle input)."
  (-> "2017/day_11.txt"
      io/resource
      slurp
      str/trim))

(def directions
  "The deltas to be applied to the coordinates of a tile in order to
  reach the tile in the specified direction. I am using cube
  coordinates to identify hex tiles as described in
  https://math.stackexchange.com/questions/2254655/hexagon-grid-coordinate-system"
  {"n"  [0 1 -1]
   "s"  [0 -1 1]
   "sw" [-1 0 1]
   "nw" [-1 1 0]
   "se" [1 -1 0]
   "ne" [1 0 -1]})

(defn follow-path
  "Given a comma-delimited series of directions, return the cell reached
  starting from the origin and following each of those directions in
  turn."
  [path]
  (->> (str/split path #",")
      (map directions)
      (reduce (partial mapv +))))

(defn distance
  "Calculate the minimum number of steps required to get from one hex
  tile to another. See https://www.redblobgames.com/grids/hexagons/#coordinates"
  [a b]
  (->> (map - a b)
       (map #(Math/abs %))
       (apply max)))

(defn part-1
  "Solve part 1: find the distance to the cell reached after following a
  path."
  ([]
   (part-1 input))
  ([path]
   (distance [0 0 0] (follow-path path))))

(defn follow-path-tracking-max-distance
  "A slightly expanded version of `follow-path` which keeps track of the
  furthest distance we have reached from the origin while traveling
  the path. Returns a tuple of that maximum distance and the final
  cell."
  [path]
  (loop [pos          [0 0 0]
         max-distance 0
         [dir & left] (str/split path #",")]
    (if dir
      (let [now (mapv + pos (directions dir))]
        (recur now
               (max max-distance (distance [0 0 0] now))
               left))
      [max-distance pos])))

(defn part-2
  "Solve part 1: find the furthest distance reached while following a
  path."
  ([]
   (part-2 input))
  ([path]
   (first (follow-path-tracking-max-distance path))))
