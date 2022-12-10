(ns advent-of-code-2022.day-5
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_5.txt")
       slurp))

(defn crates-on-line
  "Returns the crate identifiers found on a given line."
  [line]
  (->> line
       (drop 1)
       (partition 1 4)
       (map first)))

(defn push-crates
  "Given a list of crate stacks, and the crates found on the current
  line, update the stacks."
  [stacks crates]
  (let [len (max (count stacks) (count crates))]
    (map (fn [stack crate]
           (if (not= crate \space)
             (cons crate stack)
             stack))
         (take len (concat stacks (repeat '())))
         (take len (concat crates (repeat \space))))))

(defn read-crates
  "Converts the lines of crate information into the lists of crates."
  [lines]
  (->> (reduce push-crates [] (map crates-on-line lines))
       (map reverse)
       vec))

(defn read-moves
  "Extracts just the numbers (count, source, destination) from the move
  instruction lines."
  [lines]
  (map (fn [line]
         (->> (str/split line #"\s+")
              rest
              (partition 1 2)
              (map first)
              (map #(Long/parseLong %))))
       lines))

(defn read-input
  "Parses the problem input into a tuple of the crate stacks and the move
  instructions."
  [data]
  (let [[crates _ moves] (->> data
                              str/split-lines
                              (partition-by empty?))]
    [(read-crates (butlast crates)) (read-moves moves)]))

(defn move-crate
  "Move a single crate from one stack to another."
  [stacks from to]
  (let [[top & remainder] (get stacks from)]
    (-> stacks
        (assoc from remainder)
        (update to conj top))))

(defn apply-move
  "Follow a move specification, individually moving a number of crates
  from one stack to another."
  [stacks [n from to]]
  (loop [result stacks
         left n]
    (if (pos? left)
      (recur (move-crate result (dec from) (dec to)) (dec left))
      result)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [[stacks moves] (read-input data)]
     (apply str (map first (reduce apply-move stacks moves))))))

(defn apply-move-2
  "Follow a move specification for part 2, moving a number of crates as a
  group from one stack to another."
  [stacks [n from to]]
  (let [[moved remainder] (split-at n (get stacks (dec from)))]
    (-> stacks
        (assoc (dec from) remainder)
        (update (dec to) (fn [stack added] (concat added stack)) moved))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [[stacks moves] (read-input data)]
     (apply str (map first (reduce apply-move-2 stacks moves))))))

(def sample-data
  "    [D]
[N] [C]
[Z] [M] [P]
 1   2   3

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2")
