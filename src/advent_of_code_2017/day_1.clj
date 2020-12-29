(ns advent-of-code-2017.day-1
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The sequence of digits (puzzle input)."
  (str/trim (slurp (io/resource "2017/day_1.txt"))))

(defn part-1
  "Solve part 1 of the problem."
  [input]
  (as-> input digits
    (seq digits)
    (concat digits [(first digits)])
    (partition 2 1 digits)
    (filter (fn [[a b]]
              (= a b))
            digits)
    (map (fn [[a _]]
           (Long/parseLong (str a)))
         digits)
    (apply + digits)))

(defn part-2
  "Solve part 2 of the problem."
  [input]
  (let [half-1 (subs input 0 (/ (count input) 2))
        half-2 (subs input (/ (count input) 2))]
    (->> (interleave input (concat half-2 half-1))
         (partition 2)
         (filter (fn [[a b]]
                   (= a b)))
         (map (fn [[a _]]
                (Long/parseLong (str a))))
         (apply + ))))
