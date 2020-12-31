(ns advent-of-code-2017.day-12
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The list of programs and their direct connections (puzzle input)."
  (->> "2017/day_12.txt"
       io/resource
       io/reader
       line-seq))

(defn parse-line
  "Handles one line of input from the problem statement, returning a
  tuple of a program ID and the set of program IDs with which it can
  communicate directly."
  [line]
  (let [[_ prog pipes] (re-matches #"(\d+)\s+<->\s+(.*)" line)]
    [(Long/parseLong prog)
     (set (map #(Long/parseLong %) (str/split pipes #",\s*")))]))

(defn add-connectivity
  "Given a map of programs to the set of programs they are connected to,
  and a new set of programs that can communicate, update the map to
  reflect the additional connectivity."
  [known new]
  (let [existing (apply set/union (map known new))]
    (reduce (fn [acc prog]
              (update acc prog (fnil set/union #{}) new existing))
            known
            (set/union new existing))))

(defn part-1
  "Solve part 1: Read the connectivity information, building the map of
  connected groups, and find how many are in program 0's group.
  Optionally takes the lines of input in order to unit test with the
  sample data."
  ([]
   (part-1 input))
  ([lines]
   (let [groups (reduce (fn [acc [prog pipes]]
                          (add-connectivity acc (conj pipes prog)))
                        {}
                        (map parse-line lines))]
     (count (groups 0)))))

(defn part-2
  "Solve part 2: Read the connectivity information, building the map of
  connected groups, and find how many distinct groups exist.
  Optionally takes the lines of input in order to unit test with the
  sample data."
  ([]
   (part-2 input))
  ([lines]
   (let [groups (reduce (fn [acc [prog pipes]]
                          (add-connectivity acc (conj pipes prog)))
                        {}
                        (map parse-line lines))]
     (count (set (vals groups))))))
