(ns advent-of-code-2015.day-9
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.math.combinatorics :as combo]))

(def input
  "The list of distances between city pairs (the puzzle input)."
  (->> (io/resource "2015/day_9.txt")
       io/reader
       line-seq))

(defn read-distances
  "Given a list of city distance lines in the format used by the problem
  statement, build a map whose keys are sets of two cities, and whose
  values are the distance between them."
  [lines]
  (reduce (fn [acc line]
            (if-let [[_ city-1 city-2 distance] (re-matches #"(.+) to (.+) = (\d+)" line)]
              (assoc acc #{city-1 city-2} (Long/parseLong distance))
              (throw (Exception. (str "Unable to parse city distance line: " line)))))
          {}
          lines))

(defn route-length
  "Calculates the total distance required to travel a particular route
  between multiple cities, given the table of distances between city
  pairs, and the list of cities in the order they should be visited."
  [distances route]
  (->> route
       (partition 2 1)
       (map (fn [pair] (distances (set pair))))
       (apply +)))

(defn part-1
  "Solve part 1, brute force, hope the input is not too large."
  ([]
   (part-1 input))
  ([lines]
   (let [distances (read-distances lines)
         cities (apply set/union (keys distances))
         routes (combo/permutations cities)]
     (reduce (fn [acc route] (min acc (route-length distances route)))
             Long/MAX_VALUE
             routes))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([lines]
   (let [distances (read-distances lines)
         cities (apply set/union (keys distances))
         routes (combo/permutations cities)]
     (reduce (fn [acc route] (max acc (route-length distances route)))
             Long/MIN_VALUE
             routes))))
