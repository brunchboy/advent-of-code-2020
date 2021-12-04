(ns advent-of-code-2015.day-16
  "Solutions for day 16."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(def input
  "The puzzle input."
  (-> (io/resource "2015/day_16.edn")
      slurp
      edn/read-string))

(defn part-1
  "Screen out any Sues that are inconsistent with the MFCSAM report."
  []
  (filter (fn [sue]
            (and
             (= (:children sue 3) 3)
             (= (:cats sue 7) 7)
             (= (:samoyeds sue 2) 2)
             (= (:pomeranians sue 3) 3)
             (= (:akitas sue 0) 0)
             (= (:vizslas sue 0) 0)
             (= (:goldfish sue 5) 5)
             (= (:trees sue 3) 3)
             (= (:cars sue 2) 2)
             (= (:perfumes sue 1) 1)))
          input))

(defn part-2
  "Screen out any Sues that are inconsistent with the MFCSAM report,
  given the updated understanding for part 2."
  []
  (filter (fn [sue]
            (and
             (= (:children sue 3) 3)
             (> (:cats sue 8) 7)
             (= (:samoyeds sue 2) 2)
             (< (:pomeranians sue 2) 3)
             (= (:akitas sue 0) 0)
             (= (:vizslas sue 0) 0)
             (< (:goldfish sue 4) 5)
             (> (:trees sue 4) 3)
             (= (:cars sue 2) 2)
             (= (:perfumes sue 1) 1)))
          input))
