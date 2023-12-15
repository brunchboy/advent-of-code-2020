(ns advent-of-code-2023.day-6
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def sample-input
  "The sample problems, as a pair of vectors of times and distances."
  [[7  15   30]
   [9  40  200]])

(def input
  (->> (io/resource "2023/day_6.edn")
       slurp
       (edn/read-string)))

(defn count-solutions
  [race-time previous-record]
  (let [min-hold (/ (+ (- race-time) (Math/sqrt (- (* race-time race-time) (* 4.0 previous-record)))) -2.0)
        min-hold (inc (Math/floor min-hold))
        max-hold (/ (- (- race-time) (Math/sqrt (- (* race-time race-time) (* 4.0 previous-record)))) -2.0)
        max-hold (dec (Math/ceil max-hold))]
    (long (+ max-hold (- min-hold) 1))))



(defn part-1
  ([]
   (part-1 input))
  ([data]
   (let [[times records] data]
     (apply * (map count-solutions times records)))))

(def sample-input-2
  "The sample problems as reinterpreted for part 2."
  [[71530]
   [940200]])

(def input-2
  "The puzzle data as reinterpreted for part 2."
  [[58996469]
   [478223210191071]])
