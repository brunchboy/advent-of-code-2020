(ns advent-of-code-2017.day-8
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The list of instructions to run (the puzzle input)."
  (-> "2017/day_8.txt"
      io/resource
      io/reader
      line-seq))

(defn parse-instruction
  "Builds a structure supporting convenient execution of an instruction
  specified by a source line. Returns a map containing the destination
  register, the delta to be applied to that register if the test
  function succeeds, and a test function that can be called with the
  current register map, which will return truthy if the instruction
  should be executed."
  [line]
  (let [[_ dest mode delta test op val] (re-matches #"(\w+)\s+(\w+)\s+(-?\d+)\s+if\s+(\w+)\s+(\S+)\s+(-?\d+)" line)
        val                             (Long/parseLong val)]
    {:destination dest
     :delta       (case mode
                    "inc" (Long/parseLong delta)
                    "dec" (- (Long/parseLong delta)))
     :test        (case op
                    ">"  (fn [registers] (> (registers test 0) val))
                    ">=" (fn [registers] (>= (registers test 0) val))
                    "<"  (fn [registers] (< (registers test 0) val))
                    "<=" (fn [registers] (<= (registers test 0) val))
                    "==" (fn [registers] (= (registers test 0) val))
                    "!=" (fn [registers] (not (= (registers test 0) val))))}))

(defn execute
  "Given the current register map and a parsed instruction, returns the
  updated register map, which includes a special key `:maximum` which
  holds the highest value ever held in any register."
  [registers instruction]
  (let [registers (if ((:test instruction) registers)
                    (update registers (:destination instruction) (fnil + 0) (:delta instruction))
                    registers)
        maximum   (apply max (conj (vals (dissoc registers :maximum)) 0))]
    (update registers :maximum (fnil max 0) maximum)))

(defn run
  "Runs the specified set of instructions on an empty set of registers,
  returning the resulting registers."
  [input]
  (reduce execute {} (map parse-instruction input)))

(defn part-1
  "Solves part 1 by running the specified instructions and finding the
  largest resulting register value."
  [input]
  (->> input
       run
       vals
       (apply max)))

(defn part-2
  "Solves part 2 by running the specified instructions and finding the
  largest value a register ever had."
  [input]
  (-> input
      run
      :maximum))
