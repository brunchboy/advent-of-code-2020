(ns advent-of-code-2021.day-9
  "Solutions for day 9."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The puzzle input."
  (->> (io/resource "2021/day_9.txt")
       slurp
       str/split-lines
       (map (fn [line] (mapv #(Character/getNumericValue %) line)))
       vec))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([grid]
   (apply + (for [y (range (count grid))
                  x (range (count (get grid y)))]
              (let [height (get-in grid [y x])]
                (if (and (< height (get-in grid [(dec y) x] 9))
                         (< height (get-in grid [(inc y) x] 9))
                         (< height (get-in grid [y (dec x)] 9))
                         (< height (get-in grid [y (inc x)] 9)))
                  (inc height)
                  0))))))

(defn basin-size
  "Calculate the size of a connected basin, which is bounded by either
  the edge of the grid, or a cell of height 9."
  [grid x y]
  #_(println "basin-size" x y (get-in @grid [y x] 9))
  (if (> (get-in @grid [y x] 9) 8)
    0
    (do
      (swap! grid assoc-in [y x] 9)
      (+ 1
         (basin-size grid (dec x) y)
         (basin-size grid (inc x) y)
         (basin-size grid x (dec y))
         (basin-size grid x (inc y))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([grid]
   (let [markable (atom grid)]
     (->> (for [y (range (count grid))
                x (range (count (get grid y)))]
            (let [height (get-in grid [y x])]
              (when (and (< height (get-in grid [(dec y) x] 9))
                         (< height (get-in grid [(inc y) x] 9))
                         (< height (get-in grid [y (dec x)] 9))
                         (< height (get-in grid [y (inc x)] 9)))
                (basin-size markable x y))))
          (filter identity)
          (sort >)
          (take 3)
          (apply *)))))
