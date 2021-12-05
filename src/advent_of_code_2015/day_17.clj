(ns advent-of-code-2015.day-17
  "Solutions for day 17."
  (:require [clojure.math.combinatorics :as combo]))

(def input
  "The puzzle input."
  [{:id 1 :size 43}
   {:id 2 :size 3}
   {:id 3 :size 4}
   {:id 4 :size 10}
   {:id 5 :size 21}
   {:id 6 :size 44}
   {:id 7 :size 4}
   {:id 8 :size 6}
   {:id 9 :size 47}
   {:id 10 :size 41}
   {:id 11 :size 34}
   {:id 12 :size 17}
   {:id 13 :size 17}
   {:id 14 :size 44}
   {:id 15 :size 36}
   {:id 16 :size 31}
   {:id 17 :size 46}
   {:id 18 :size 9}
   {:id 19 :size 27}
   {:id 20 :size 38}])

(defn part-1
  "Solve part 1"
  ([]
   (part-1 input 150))
  ([data eggnog]
   (count (filter #(= eggnog (apply + (map :size %))) (combo/subsets data)))))

(defn part-2
  "Solve part 2"
  ([]
   (part-2 input 150))
  ([data eggnog]
   (let [candidates (filter #(= eggnog (apply + (map :size %))) (combo/subsets data))
         minimum (apply min (map count candidates))]
     (count (filter #(= minimum (count %)) candidates)))))
