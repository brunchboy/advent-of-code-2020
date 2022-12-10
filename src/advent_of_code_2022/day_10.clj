(ns advent-of-code-2022.day-10
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_10.txt")
       slurp))

(def initial-state
  "The starting state of the CPU."
  {:pc    0
   :cycle 0
   :spent 0
   :x     1})

(defn read-program-line
  "Parses a line of problem input into a tuple of an opcode keyword and
  its possible operand."
  [line]
  (let [[opcode operand] (str/split line #"\s+")]
    (case opcode
      "noop" [:noop]
      "addx" [:addx (Long/parseLong operand)])))

(defn read-program
  "Reads the entire program (problem input) into a vector of opcode
  keywords and optional operand values."
  [text]
  (->> text
       (str/split-lines)
       (mapv read-program-line)))

(defn step
  "Perform a cycle of the simulated processor."
  [{:keys [pc spent program] :as state}]
  (let [[opcode operand] (get program pc)]
    (case opcode
      :noop (-> state
                (update :pc inc)
                (update :cycle inc)
                (assoc :spent 0))
      :addx (if (zero? spent)
              (-> state
                  (update :cycle inc)
                  (update :spent inc))
              (-> state
                  (update :cycle inc)
                  (update :pc inc)
                  (update :x + operand)
                  (assoc :spent 0)))
      nil   state)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [cycles (iterate step (assoc initial-state :program (read-program data)))]
     (apply + (for [cycle [20 60 100 140 180 220]]
                (* cycle (:x (nth cycles (dec cycle)))))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (->> (map (fn [state pixel]
               (if (< (Math/abs (- (mod pixel 40) (:x state))) 2)
                 "#"
                 "."))
             (iterate step (assoc initial-state :program (read-program data)))
             (range 240))
        (partition 40)
        (map #(apply str %)))))

(def sample-input
  "The puzzle sample input."
  "noop
addx 3
addx -5")

(def sample-input-2
  "Larger sample input."
  "addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop")
