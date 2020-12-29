(ns advent-of-code-2017.day-5
  (:require [clojure.java.io :as io]))

(defn step
  "Process one step of the simplistic processor described by the problem
  statement for part 1. Inputs are a tuple containing the vector of
  branch offsets, the current instruction pointer, and the count of
  branches that have been performed. Returns the next state of each."
  [[offsets addr n]]
  [(update offsets addr inc) (+ addr (offsets addr)) (inc n)])

(def input
  "The list of branch offsets (puzzle input)."
  (->> "2017/day_5.txt"
       io/resource
       io/reader
       line-seq
       (mapv #(Long/parseLong %))))

(defn part-1
  "Count how many steps it takes until we branch outside the program."
  [offsets]
  (->> (iterate step [offsets 0 0])
       (drop-while (fn [[offsets addr]]
                     (< addr (count offsets))))
       first
       last))

(defn step-2
  "Process one step of the bizarre yet still simplistic processor
  described by the problem statement for part 2. Inputs are a tuple
  containing the vector of branch offsets, the current instruction
  pointer, and the count of branches that have been performed. Returns
  the next state of each."
  [[offsets addr n]]
  [(update offsets addr (fn [offset]
                          (if (>= offset 3)
                            (dec offset)
                            (inc offset))))
   (+ addr (offsets addr))
   (inc n)])

(defn part-2
  "Count how many part-2 style steps it takes until we branch outside
  the program."
  [offsets]
  (->> (iterate step-2 [offsets 0 0])
       (drop-while (fn [[offsets addr]]
                     (< addr (count offsets))))
       first
       last))
