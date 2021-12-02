(ns advent-of-code-2021.day-2
  "Solutions for day 2."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn step
  "Process one step line of the submarine movement instructions."
  [[pos depth] s]
  (let [[direction distance] (str/split s #"\s+")
        distance             (Long/parseLong distance)]
    (case direction
      "forward" [(+ pos distance) depth]
      "down"    [pos (+ depth distance)]
      "up"      [pos (- depth distance)])))

(defn move
  "Process the lines of the submarine movement instructions, returning
  the final position."
  [input]
  (reduce step [0 0] (str/split-lines input)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 (slurp (io/resource "2021/day_2.txt"))))
  ([input]
   (apply * (move input))))

(defn step-2
  "Process one step line of the submarine movement instructions, using
  the aim-based interpretation of part 2."
  [[pos depth aim] s]
  (let [[direction distance] (str/split s #"\s+")
        distance             (Long/parseLong distance)]
    (case direction
      "forward" [(+ pos distance) (+ depth (* aim distance)) aim]
      "down"    [pos depth (+ aim distance)]
      "up"      [pos depth (- aim distance)])))

(defn move-2
  "Process the lines of the submarine movement instructions, using the
  aim-based interpretation of part 2, returning the final position and
  aim."
  [input]
  (reduce step-2 [0 0 0] (str/split-lines input)))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 (slurp (io/resource "2021/day_2.txt"))))
  ([input]
   (apply * (take 2 (move-2 input)))))
