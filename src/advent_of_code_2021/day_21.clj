(ns advent-of-code-2021.day-21
  "Solutions for day 21."
  (:require [clojure.math.combinatorics :as combo]))

(defn move
  "Given a starting space and a series of rolls, determine the space on
  which that player ends up, on the circular board."
  [space rolls]
  (let [distance (apply + rolls)]
    (-> space
        dec
        (+ distance)
        (mod 10)
        inc)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 7 4))
  ([player-1 player-2]
   (loop [score-1 0
          score-2 0
          space-1 player-1
          space-2 player-2
          rolls   (->> (range 1 101) cycle (partition 3))
          rolled  3]
     (let [space-1 (move space-1 (first rolls))
           score-1 (+ score-1 space-1)]
       (if (>= score-1 1000)
         (* score-2 rolled)
         (let [space-2 (move space-2 (second rolls))
               score-2 (+ score-2 space-2)]
           (if (>= score-2 1000)
             (* score-1 (inc rolled))
             (recur score-1
                    score-2
                    space-1
                    space-2
                    (drop 2 rolls)
                    (+ 6 rolled)))))))))

(def wins-from
  "Count the number of ways each player can win from a given starting
  position and score. If the either player has already won, then the
  answer is trivial: just one. Otherwise, consider each possible
  combination of rolls the first player can make, and add up the wins
  that can be achieved from that resulting board state. Use
  memoization because we are going to see lots of repeated states, and
  we don't want the computation to take forever."
  (memoize
   (fn [space-1 score-1 space-2 score-2]
     (cond
       (>= score-1 21)
       [1 0]

       (>= score-2 21)
       [0 1]

       :else
       (reduce (fn [[wins-1 wins-2] rolls]
                 (let [space-1 (move space-1 rolls)
                       score-1 (+ score-1 space-1)
                       ;; We can swap the notion of the first and second player in the
                       ;; memoized recursive call so we don't have to write two versions
                       ;; of the function.
                       [new-wins-2 new-wins-1] (wins-from space-2 score-2 space-1 score-1)]
                   [(+ wins-1 new-wins-1) (+ wins-2 new-wins-2)]))
               [0 0]
               (combo/selections [1 2 3] 3))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 7 4))
  ([player-1 player-2]
   (apply max (wins-from player-1 0 player-2 0))))
