(ns advent-of-code-2020.day-5
  "Solutions to the day 5 problems."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The boarding passes I've seen (the puzzle input)."
  (str/split-lines (slurp (io/resource "day_5.txt"))))

(defn seat-code
  "Converts a string seat code to the corresponding integer."
  [code]
  (let [code (-> code
                 (str/replace #"F|L" "0")
                 (str/replace #"B|R" "1"))
        row  (Long/parseLong (subs code 0 7) 2)
        col  (Long/parseLong (subs code 7) 2)]
    (+ (* 8 row) col)))

(defn part-1
  "Find the largest seat code in the puzzle input."
  []
  (apply max (map seat-code input)))

(defn my-seat?
  "Test whether a seat number might be my seat, that is, the puzzle
  input includes a seat code that is one less than it, and one greater
  than it, but does not include the supplied seat code itself."
  [seen seat]
  (and (seen (dec seat))
       (not (seen seat))
       (seen (inc seat))))

(defn part-2
  "Find any seat codes that corresponds to my seat, according to the
  puzzle rules. (There should be only one returned.)"
  []
  (let [seen (set (map seat-code input))]
    (filter (partial my-seat? seen) (range (part-1)))))
