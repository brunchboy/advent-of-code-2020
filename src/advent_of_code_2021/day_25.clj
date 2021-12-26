(ns advent-of-code-2021.day-25
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The puzzle input."
  (->> (io/resource "2021/day_25.txt")
       slurp
       str/split-lines
       (mapv vec)))

(defn move-horizontals
  "Move all of the horizontal-facing sea cucumbers which can."
  [lines]
  (let [width  (count (first lines))
        height (count lines)]
    (loop [y      0
           x      0
           result lines]
      (let [next-x (mod (inc x) width)
            result (if (and (= \> (get-in lines [y x]))
                            (= \. (get-in lines [y next-x])))
                     (-> result (assoc-in [y x] \.)
                         (assoc-in [y next-x] \>))
                     result)]
               (if (< x (dec width))
                 (recur y (inc x) result)
                 (if (< y (dec height))
                   (recur (inc y) 0 result)
                   result))))))

(defn move-verticals
  "Move all of the vertical facing sea cucumbers which can."
  [lines]
  (let [width  (count (first lines))
        height (count lines)]
    (loop [y      0
           x      0
           result lines]
      (let [next-y (mod (inc y) height)
            result (if (and (= \v (get-in lines [y x]))
                            (= \. (get-in lines [next-y x])))
                     (-> result
                         (assoc-in [y x] \.)
                         (assoc-in [next-y x] \v))
                     result)]
        (if (< x (dec width))
          (recur y (inc x) result)
          (if (< y (dec height))
            (recur (inc y) 0 result)
            result))))))

(defn step
  "Iterate one step of sea cucumber movement."
  [lines]
  (-> lines
      move-horizontals
      move-verticals))

(defn steps-to-convergence
  "Count how many steps it takes for the sea cucumbers to stop moving."
  [lines]
  (reduce (fn [{:keys [count prev]} cucumbers]
            (if (= prev cucumbers)
              (reduced count)
              {:count (inc count)
               :prev cucumbers}))
          {:count 0}
          (iterate step lines)))
