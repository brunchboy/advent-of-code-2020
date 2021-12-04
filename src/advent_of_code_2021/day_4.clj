(ns advent-of-code-2021.day-4
  "Solutions for day 4."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.edn :as edn]))

(defn build-boards
  "Given the list of numbers found on the bingo boards, build up a map
  that makes them easy to work with. Keys in the map are board number,
  then x coordinate, then y coordinate, and the value at that point is
  the number within that board cell. At the same time, build up an
  index of where each number is found on any board, under the key
  `:number-index`, followed by the number, and values are a vector
  of [board x y] tuples identifying places that number is found on the
  boards."
  [xs]
  (let [cell-seq (interleave
                  xs
                  (flatten (for [board (range)] (repeat 25 board)))
                  (cycle (range 5))
                  (cycle (flatten (for [y (range 5)] (repeat 5 y)))))]
    (reduce (fn [acc [n board x y]]
              (-> acc
                  (update-in [:number-index n] (fnil conj []) [board x y])
                  (assoc-in [board x y] n)))
            {}
            (partition 4 cell-seq))))

(defn mark-number
  "Removes all instances of the specified number from the bingo boards,
  because it has been called."
  [boards n]
  (loop [boards    boards
         to-remove (get-in boards [:number-index n])]
    (if (empty? to-remove)
      boards
      (let [[board x y] (first to-remove)]
        (recur (update-in boards [board x] dissoc y)
               (rest to-remove))))))

(defn row-numbers
  "Returns all the unmarked numbers left in the specified row."
  [boards board y]
  (->> (for [pos (range 5)]
         (get-in boards [board pos y]))
       (filter identity)))

(defn column-numbers
  "Returns all the unmarked numbers left in the specified column."
  [boards board x]
  (->> (for [pos (range 5)]
         (get-in boards [board x pos]))
       (filter identity)))

(defn won?
  "Checks whether a move has resulted in a board being won, because
  there are now no unmarked numbers in either the corresponding row or
  column."
  [boards [board x y]]
  (or (empty? (row-numbers boards board y)) (empty? (column-numbers boards board x))))

(defn board-numbers
  "Returns all the unmarked numbers left in the specified board."
  [boards board]
  (->> (for [x (range 5)
             y (range 5)]
         (get-in boards [board x y]))
       flatten
       (filter identity)))

(def calls
  "The numbers called (from the problem input)."
  [4 77 78 12 91 82 48 59 28 26 34 10 71 89 54 63 66 75 15 22 39 55 83 47 81 74 2 46 25 98 29 21 85 96 3 16 60
   31 99 86 52 17 69 27 73 49 95 35 9 53 64 88 37 72 92 70 5 65 79 61 38 14 7 44 43 8 42 45 23 41 57 80 51 90
   84 11 93 40 50 33 56 67 68 32 6 94 97 13 87 30 18 76 36 24 19 20 1 0 58 62])

(defn part-1
  "Find the first board to be won, and then calculate the corresponding
  score, following the rules of the problem statement."
  ([]
   (part-1 (build-boards (edn/read-string (slurp (io/resource "2021/day_4.edn")))) calls))
  ([boards calls]
   (loop [boards boards
          calls  calls]
     (let [boards (mark-number boards (first calls))
           winner (first (filter (partial won? boards) (get-in boards [:number-index (first calls)])))]
       (if winner
         (* (first calls) (apply + (board-numbers boards (first winner))))
         (recur boards (rest calls)))))))

(defn won-2?
  "Check whether a move has resulted in a board being won, because it no
  longer has any unmarked cells in either the corresponding row or
  column, but ignore boards that are not in play, because they have
  previously been won."
  [boards in-play [board x y]]
  (when (in-play board)
    (or (empty? (row-numbers boards board y)) (empty? (column-numbers boards board x)))))

(defn part-2
  "Find the last board to be won, and then calculate the corresponding
  score, following the rules of the problem statement. Although this
  supports multiple boards being won for intermediate moves, it
  assumes the final board is the only one that is won by the winning
  play."
  ([]
   (part-2 (build-boards (edn/read-string (slurp (io/resource "2021/day_4.edn")))) calls))
  ([boards calls]
   (loop [boards  boards
          calls   calls
          in-play (set (filter number? (keys boards)))]
     (let [boards  (mark-number boards (first calls))
           winners (filter (partial won-2? boards in-play) (get-in boards [:number-index (first calls)]))]
       (if (seq winners)
         (let [boards-won (map first winners)
               in-play    (set/difference in-play (set boards-won))]
           (if (empty? in-play)
             (* (first calls) (apply + (board-numbers boards (last boards-won))))
             (recur boards (rest calls) in-play)))
         (recur boards (rest calls) in-play))))))
