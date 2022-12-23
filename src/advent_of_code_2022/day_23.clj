(ns advent-of-code-2022.day-23
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_23.txt")
       slurp
       str/split-lines
       vec))

(defn read-input
  "Reads the input, building a set of the elf positions."
  [data]
  (->> (for [row    (range (count data))
             column (range (count (first data)))]
         (when (= \# (get-in data [row column]))
           [column row]))
       (filter identity)
       set))

(defn elves-in-direction
  "Return the set of elves that are visible in the specified direction,
  according to the problem statement."
  [elves [elf-x elf-y] direction]
  (let [candidates (case direction
                     "N" #{[(dec elf-x) (dec elf-y)] [elf-x (dec elf-y)] [(inc elf-x) (dec elf-y)]}
                     "S" #{[(dec elf-x) (inc elf-y)] [elf-x (inc elf-y)] [(inc elf-x) (inc elf-y)]}
                     "W" #{[(dec elf-x) (dec elf-y)] [(dec elf-x) elf-y] [(dec elf-x) (inc elf-y)]}
                     "E" #{[(inc elf-x) (dec elf-y)] [(inc elf-x) elf-y] [(inc elf-x) (inc elf-y)]})]
    (set/intersection elves candidates)))

(defn uncrowded?
  "Checks whether an elf has no neighbors, and therefore has no need to
  move."
  [elves elf]
  (let [neighbors (map (partial elves-in-direction elves elf) ["N" "S" "W" "E"])]
    (empty? (reduce set/union #{} neighbors))))

(defn first-move
  "Find the first direction that an elf can move, according to the
  problem statement, considering directions in the current priority
  order."
  [elves elf order]
  (loop [[direction & remaining] order]
    (when direction
      (if (empty? (elves-in-direction elves elf direction))
        (case direction
          "N" (update elf 1 dec)
          "S" (update elf 1 inc)
          "W" (update elf 0 dec)
          "E" (update elf 0 inc))
        (recur remaining)))))

(defn round
  "Implement one round of the elf expansion rules."
  [{:keys [elves order] :as state}]
  (let [proposals (reduce (fn [acc elf]
                            (if (uncrowded? elves elf)
                              acc
                              (if-let [proposal (first-move elves elf order)]
                                (update acc proposal (fnil conj #{}) elf)
                                acc)))
                          {}
                          elves)]
    (-> state
        (assoc :order (concat (rest order) (take 1 order)))
        (assoc :elves (reduce (fn [acc [proposal proponents]]
                                (if (> (count proponents) 1)
                                  acc
                                  (-> acc
                                      (disj (first proponents))
                                      (conj proposal))))
                              elves
                              proposals))
        (update :round inc)
        (assoc :moving? (not-empty proposals)))))

(defn count-empty-ground
  "Find the bounding box of the elves, and count how many squares within
  are unoccupied."
  [{:keys [elves]}]
  (let [min-x (->> elves (map first) (apply min))
        min-y (->> elves (map second) (apply min))
        max-x (->> elves (map first) (apply max))
        max-y (->> elves (map second) (apply max))]
    (- (* (inc (- max-x min-x)) (inc (- max-y min-y))) (count elves))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [state {:elves (read-input data)
                :order (list "N" "S" "W" "E")}]
     (->> (iterate round state)
          (drop 10)
          first
          count-empty-ground))))

(def sample-input
  "The test data."
  (vec (str/split-lines "....#..
..###.#
#...#.#
.#...##
#.###..
##.#.##
.#..#..")))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [state {:elves   (read-input data)
                :order   (list "N" "S" "W" "E")
                :moving? true
                :round   0}]
     (->> (iterate round state)
          (drop-while :moving?)
          first
          :round))))
