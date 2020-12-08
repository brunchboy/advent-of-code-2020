(ns advent-of-code-2020.day-8
  "Solutions to the day 8 problems."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The program source (the puzzle input)."
  (->> (io/resource "day_8.txt")
       io/reader
       line-seq))

(defn parse-instructions
  "Converts a sequence of instruction lines into an array of
  opcode/operand tuples."
  [lines]
  (vec (map (fn [line]
              (let [[opcode operand] (str/split line #"\s+")]
                [(keyword opcode) (Long/parseLong operand)]))
            lines)))

(defn step
  "Evaluates the next instruction in a program. Takes the parsed
  program, the current execution address and accumulator value,
  and returns the next values of all three."
  [program addr acc]
  (case (get-in program [addr 0])
    :nop
    [program (inc addr) acc]

    :acc
    [program (inc addr) (+ acc (get-in program [addr 1]))]

    :jmp
    [program (+ addr (get-in program [addr 1])) acc]))

(defn part-1
  "Steps the program until we reach a particular address for the second
  time, then returns the value of the accumulator at that point."
  ([]
   (part-1 input))
  ([source]
   (let [program (parse-instructions source)]
     (loop [addr 0
            acc  0
            seen #{}]
       (if (seen addr)
         acc
         (let [[_ new-addr acc] (step program addr acc)]
           (recur new-addr acc (conj seen addr))))))))

(defn try-version
  "Steps the program until we either reach an address for the second
  time or go off the end of the program, and returns the reason we
  stopped, along with the value of the accumulator at that point.
  Although not needed by the problem, we distinguish between hitting
  the address just past the program, which we consider success, and a
  more distant address, which we consider an overflow."
  [program]
  (loop [addr 0
         acc  0
         seen #{}]
    (cond (seen addr)
          [:loop acc]

          (> addr (count program))
          [:overflow acc]

          (= addr (count program))
          [:success acc]

          :else
          (let [[_ new-addr acc] (step program addr acc)]
            (recur new-addr acc (conj seen addr))))))

(defn part-2
  "Replace each of the jmp or nop instructions found in the
  opposite (just one at a time), running the program after each
  substitution, to see whether it results in successful termination.
  Return the status and accumulator when success was obtained."
  []
  (let [program (parse-instructions input)]
    (filter (fn [[status]] (= status :success))
            (for [addr (range (count program))]
              (case (get-in program [addr 0])
                :nop
                (try-version (assoc-in program [addr 0] :jmp))

                :jmp
                (try-version (assoc-in program [addr 0] :nop))

                nil)))))
