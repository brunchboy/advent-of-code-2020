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
  "Evaluates the next instruction in a program. Takes the current
  program state, which includes the current execution address and
  accumulator value, and returns an updated version to reflect the
  result of the current instruction."
  [{:keys [program addr] :as state}]
  (case (get-in program [addr 0])
    :nop
    (update state :addr inc)

    :acc
    (-> state
        (update :addr inc)
        (update :acc + (get-in program [addr 1])))

    :jmp
    (update state :addr + (get-in program [addr 1]))))

(defn tracked-step
  "In addition to evaluating the next instruction (using `step`
  above), keeps track of all addresses that have been executed."
  [state]
  (-> state
      (update :seen (fnil conj #{}) (:addr state))
      step))

(defn part-1
  "Steps the program until we reach a particular address for the second
  time, then returns the value of the accumulator at that point."
  ([]
   (part-1 input))
  ([source]
   (->> (iterate tracked-step {:program (parse-instructions source)
                               :addr    0
                               :acc     0
                               :seen    #{}})
        (drop-while (fn [state]
                      (not ((:seen state) (:addr state)))))
        first
        :acc)))

(defn try-version
  "Steps the program until we either reach an address for the second
  time or go off the end of the program. Returns the resulting state."
  [program]
  (->> (iterate tracked-step {:program program
                              :addr    0
                              :acc     0
                              :seen    #{}})
        (drop-while (fn [state]
                      (and (not ((:seen state) (:addr state)))
                           (< (:addr state) (count program)))))
        first))

(defn part-2
  "Replace each of the jmp or nop instructions found in the
  opposite (just one at a time), running the program after each
  substitution, to see whether it results in successful
  termination (indicated by reaching the address just past the final
  program instruction). Return the accumulator when success was
  obtained."
  []
  (let [program (parse-instructions input)]
    (->> (for [addr (range (count program))]
              (case (get-in program [addr 0])
                :nop
                (try-version (assoc-in program [addr 0] :jmp))

                :jmp
                (try-version (assoc-in program [addr 0] :nop))

                nil))
         (filter (fn [state] (= (:addr state) (count program))))
         first
         :acc)))
