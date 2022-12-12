(ns advent-of-code-2022.day-12
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_12.txt")
       slurp))

(defn read-cell
  "Read a cell of puzzle input, recording its height, and if it was the
  start or end position, making a note of that."
  [{:keys [x y] :as state} c]
  (-> (case c
        \S (-> state
               (assoc-in [:heights [x y]] 0)
               (assoc :start [x y]))
        \E (-> state
               (assoc-in [:heights [x y]] 25)
               (assoc :end [x y]))
        (assoc-in state [:heights [x y]] (- (int c) (int \a))))
      (update :x inc)))

(defn read-row
  "Read a line of puzzle input, building the map of heights and recording
  the start and/or end position if seen."
  [state line]
  (-> (reduce read-cell
              (assoc state :x 0)
              line)
      (dissoc :x)
      (update :y inc)))

(defn read-input
  "Read the lines of puzzle input, turning them into a map of heights as
  well as the starting and ending coordinates."
  [data]
  (-> (reduce read-row
              {:y 0}
              (str/split-lines data))
      (dissoc :y)))

(defn start
  "Set up the initial conditions for finding the shortest path from the
  starting point to the end point."
  [state]
  (assoc state :paths [{:position (:start state)
                        :moves    0}]
         :visited  #{(:start state)}))

(defn adjacent
  "Returns the coordinates that are reachable from a cell in a
  single horizontal move."
  [[x y]]
  [[(dec x) y] [x (dec y)] [(inc x) y] [x (inc y)]])

(defn reachable?
  "Checks whether we can move to a cell from our current height, also
  rejecting it if we have already visited it."
  [height state visited cell]
  (when-let [target (get-in state [:heights cell])]
    (and (not (visited cell))
         (<= target (inc height)))))

(defn expand-first
  [{:keys [visited] :as state}]
  (let [{:keys [position moves]} (first (:paths state))
        height                   (get-in state [:heights position])
        candidates               (filter (partial reachable? height state visited) (adjacent position))
        new-paths                (for [cell candidates]
                                   {:position cell
                                    :moves    (inc moves)
                                    :visited  (conj visited cell)})]
    ;; I was thinking I may need to sort by moves, but since we are always adding longer paths to the end,
    ;; and working from the front, I now think that's unnecessary.
    (-> state
        (update :paths (fn [paths] (concat (rest paths) new-paths)))
        (update :visited set/union (set (map :position new-paths))))))

(defn solved?
  "If we have found a path to the target cell, return it."
  [state]
  (first (filter #(= (:end state) (:position %)) (:paths state))))

(defn solve
  "Find the shortest path."
  [state]
  (loop [state (expand-first state)]
     (if-let [end (solved? state)]
       (:moves end)
       (when-not (empty? (:paths state))
         (recur (expand-first state))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (solve (start (read-input data)))))

(defn start-2
  "Set up the initial conditions for finding the shortest path from any
  lowest point to the end point."
  [{:keys [heights] :as state}]
  (let [starts (->> (filter (fn [[_ height]] (zero? height)) heights)
                    (map first))]
    (assoc state :paths (for [cell starts]
                          {:position cell
                           :moves    0})
           :visited  (set starts))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (solve (start-2 (read-input data)))))

(def sample-input
  "Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi")
