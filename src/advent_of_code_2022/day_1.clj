(ns advent-of-code-2022.day-1
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_1.txt")
       slurp))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (->> data
        str/split-lines
        (partition-by empty?)
        (take-nth 2)
        (map (fn [lines] (apply + (map #(Long/parseLong %) lines))))
        (apply max))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (->> data
        str/split-lines
        (partition-by empty?)
        (take-nth 2)
        (map (fn [lines] (apply + (map #(Long/parseLong %) lines))))
        (sort >)
        (take 3)
        (apply +))))

(def sample-data
  "1000
2000
3000

4000

5000
6000

7000
8000
9000

10000")
