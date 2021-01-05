(ns advent-of-code-2017.day-22
  (:require [clojure.java.io :as io]))

(def input
  "The initial infected node map (puzzle input)."
  (-> "2017/day_22.txt"
      io/resource
      io/reader
      line-seq))

(defn process-row
  "Converts a row of the input diagram into a list of the coordinates of
  the active cells."
  [y row]
  (->> (map-indexed (fn [x c]
                      (when (= c \#) [x y]))
                    row)
       (filter identity)))

(defn read-map
  "Reads the map provided as puzzle input and sets up the starting state
  for iteration."
  [input]
  {:virus   (set (apply concat (map-indexed process-row input)))
   :x       (quot (count (first input)) 2)
   :y       (quot (count input) 2)
   :heading :up
   :added   0})

(defn turn
  "Given the current grid state, turns the virus carrier in the
  appropriate direction."
  [{:keys [x y virus heading] :as state}]
  (assoc state :heading (if (virus [x y])
                          (case heading
                            :up    :right
                            :right :down
                            :down  :left
                            :left  :up)
                          (case heading
                            :up    :left
                            :right :up
                            :down  :right
                            :left  :down))))

(defn infect
  "Toggles the infected state of the current grid node. Also counts each
  time a node gets newly infected."
  [{:keys [x y virus] :as state}]
  (if (virus [x y])
    (update state :virus disj [x y])
    (-> state
        (update :virus conj [x y])
        (update :added inc))))

(defn move
  "Moves the virus carrier one step in the direction it is heading."
  [{:keys [x y heading] :as state}]
  (-> state
      (assoc :x (case heading
                  (:up :down) x
                  :left       (dec x)
                  :right      (inc x)))
      (assoc :y (case heading
                  (:left :right) y
                  :up            (dec y)
                  :down          (inc y)))))

(defn burst
  "Implements one burst of activity by the virus carrier. Given a
  starting state, returns the updated state."
  [state]
  (-> state
      turn
      infect
      move))

(defn state-sequence
  "Given a starting map, returns a lazy sequence of the states that will
  result from each burst of virus carrier activity as time goes on."
  [input]
  (iterate burst (read-map input)))

(defn part-1
  "Solve part 1 of the problem by returning the number of nodes newly
  infected in the course of 10,000 bursts of activity for the given
  starting map."
  [input]
  (:added (nth (state-sequence input) 10000)))

(defn mark-infected
  "Given a list of cell cordinates, returns a map where each cell is
  marked as infected."
  [cells]
  (reduce (fn [acc cell] (assoc acc cell :infected))
          {}
          cells))

(defn read-map-2
  "Reads the map provided as puzzle input and sets up the starting state
  for iteration according to the rules for part 2."
  [input]
  {:virus   (mark-infected (apply concat (map-indexed process-row input)))
   :x       (quot (count (first input)) 2)
   :y       (quot (count input) 2)
   :heading :up
   :added   0})

(defn turn-2
  "Given the current grid state, turns the virus carrier in the
  appropriate direction."
  [{:keys [x y virus heading] :as state}]
  (assoc state :heading (case (get virus [x y] :clean)
                          :infected (case heading
                                      :up    :right
                                      :right :down
                                      :down  :left
                                      :left  :up)
                          :clean    (case heading
                                      :up    :left
                                      :right :up
                                      :down  :right
                                      :left  :down)
                          :weakened heading
                          :flagged  (case heading
                                      :up    :down
                                      :right :left
                                      :down  :up
                                      :left  :right))))

(defn update-virus
  "Updates the infection state of the current grid node according to the
  more complex rules of part 2. Also counts each time a node gets
  newly infected."
  [{:keys [x y virus] :as state}]
  (case (get virus [x y] :clean)
    :clean    (update state :virus assoc [x y] :weakened)
    :weakened (-> state
                  (update :virus assoc [x y] :infected)
                  (update :added inc))
    :infected (update state :virus assoc [x y] :flagged)
    :flagged  (update state :virus dissoc [x y])))

(defn burst-2
  "Implements one burst of activity by the virus carrier according to
  the rules of part 2. Given a starting state, returns the updated
  state."
  [state]
  (-> state
      turn-2
      update-virus
      move))

(defn state-sequence-2
  "Given a starting map, returns a lazy sequence of the states that will
  result from each burst of virus carrier activity as time goes on
  following the rules of part 2."
  [input]
  (iterate burst-2 (read-map-2 input)))

(defn part-2
  "Solve part 1 of the problem by returning the number of nodes newly
  infected in the course of 10,000,000 bursts of activity for the
  given starting map."
  [input]
  (:added (nth (state-sequence-2 input) 10000000)))
