(ns advent-of-code-2015.day-3
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The list of moves (the puzzle input)."
  (->> (io/resource "2015/day_3.txt")
       slurp
       str/trim))

(defn move
  "Given our current state and a direction, update the to reflect that
  we moved in that direction and delivered a gift and return that. The
  state is a map holding our current :x and :y coordinates, as well
  as :gifts, a nested map whose keys are the coordinates of houses and
  whose values are the number of gifts that have been delivered. "
  [{:keys [gifts x y]} direction]
  (let [[x y] (case direction
                \< [(dec x) y]
                \> [(inc x) y]
                \^ [x (dec y)]
                \v [x (inc y)])]
    {:x     x
     :y     y
     :gifts (update gifts [x y] (fnil inc 0))}))

(defn apply-moves
  "Given a starting state and a list of moves, returns the resulting
  state after all of them have been applied."
  [state moves]
  (reduce (fn [acc direction]
            (move acc direction))
          state
          moves))

(defn part-1
  "Solve part 1."
  []
  (-> (apply-moves {:x     0
                    :y     0
                    :gifts {[0 0] 1}}
                    input)
       :gifts
       count))

(defn part-2
  "Solve part 2."
  []
  (let [after-santa (apply-moves {:x     0
                                  :y     0
                                  :gifts {[0 0] 1}}
                                 (take-nth 2 input))]
    (-> (apply-moves {:x     0
                      :y     0
                      :gifts (:gifts after-santa)}
                     (take-nth 2 (rest input)))
        :gifts
        count)))
