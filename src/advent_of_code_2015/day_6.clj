(ns advent-of-code-2015.day-6
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn parse-instruction
  "Given an input line, parses out the operation and corner
  coordinates, returning a map describing them."
  [line]
  (if-let [[_ op x1 y1 x2 y2] (re-matches #"(turn on|turn off|toggle)\s+(\d+),(\d+)\s+through\s+(\d+),(\d+)" line)]
    {:operation op
     :x1 (Long/parseLong x1)
     :y1 (Long/parseLong y1)
     :x2 (Long/parseLong x2)
     :y2 (Long/parseLong y2)}
    (throw (Exception. (str "Unparseable lighting instruction: " line)))))

(def input
  "The list of lighting instructions (the puzzle input)."
  (->> (io/resource "2015/day_6.txt")
       io/reader
       line-seq
       (map parse-instruction)))

(defn update-light
  "Given an operation code, the current grid state and a tuple of light
  coordinates, returns the grid that results from applying the
  specified lighting operation on the light at those coordinates."
  [operation grid light]
  (case operation
    "turn on"  (conj grid light)
    "turn off" (disj grid light)
    "toggle"   (if (grid light) (disj grid light) (conj grid light))))

(defn update-grid
  "Applies a single lighting instruction to the grid of lights."
  [grid {:keys [operation x1 y1 x2 y2]}]
  (reduce (partial update-light operation) grid (for [x (range x1 (inc x2))
                                                      y (range y1 (inc y2))]
                                                  [x y])))

(defn part-1
  "Solve part 1"
  []
  (->> (reduce update-grid #{} input)
       count))

(defn update-light-2
  "Given an operation code, the current grid state and a tuple of light
  coordinates, returns the grid that results from applying the
  specified lighting operation (according to the rules of part two) on
  the light at those coordinates."
  [operation grid light]
  (case operation
    "turn on"  (update grid light (fnil inc 0))
    "turn off" (update grid light (fn [level] (max 0 (dec (or level 0)))))
    "toggle"   (update grid light (fnil #(+ % 2) 0))))

(defn update-grid-2
  "Applies a single lighting instruction using the part two rules to the
  grid of lights."
  [grid {:keys [operation x1 y1 x2 y2]}]
  (reduce (partial update-light-2 operation) grid (for [x (range x1 (inc x2))
                                                        y (range y1 (inc y2))]
                                                    [x y])))

(defn part-2
  "Solve part 2"
  []
  (let [grid (reduce update-grid-2 {} input)]
    (apply + (vals grid))))
