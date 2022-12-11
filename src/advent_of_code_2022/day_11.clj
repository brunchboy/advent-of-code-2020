(ns advent-of-code-2022.day-11
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def sample-monkeys
  "The sample data. LCM of these tests is 96577."
  [{:id        0
    :items     [79 98]
    :operation #(* % 19)
    :op-2      #(* (mod % 23) (mod 19 23))
    :test      23
    :targets   [2 3]}
   {:id        1
    :items     [54 65 75 74]
    :operation #(+ % 6)
    :test      19
    :targets   [2 0]}
   {:id        2
    :items     [79 60 97]
    :operation #(* % %)
    :test      13
    :targets   [1 3]}
   {:id        3
    :items     [74]
    :operation #(+ % 3)
    :test      17
    :targets   [0 1]}])

(def input
  "The sample data. LCM for these tests is 9699690."
  [{:id        0
    :items     [98 89 52]
    :operation #(* % 2)
    :test      5
    :targets   [6 1]}
   {:id        1
    :items     [57, 95, 80, 92, 57, 78]
    :operation #(* % 13)
    :test      2
    :targets   [2 6]}
   {:id        2
    :items     [82, 74, 97, 75, 51, 92, 83]
    :operation #(+ % 5)
    :test      19
    :targets   [7 5]}
   {:id        3
    :items     [97, 88, 51, 68, 76]
    :operation #(+ % 6)
    :test      7
    :targets   [0 4]}
   {:id        4
    :items     [63]
    :operation #(inc %)
    :test      17
    :targets   [0 1]}
   {:id        5
    :items     [94, 91, 51, 63]
    :operation #(+ % 4)
    :test      13
    :targets   [4 3]}
   {:id        6
    :items     [61, 54, 94, 71, 74, 68, 98, 83]
    :operation #(+ % 2)
    :test      3
    :targets   [2 7]}
   {:id        7
    :items     [90, 56]
    :operation #(* % %)
    :test      11
    :targets   [3 5]}])

(defn round
  "Perform one round of monkey business."
  [monkeys]
  (loop [i     0
         state monkeys]
    (if (< i (count monkeys))
      (let [monkey (nth state i)]
        (if (empty? (:items monkey))
          (recur (inc i) state)
          (let [[current & remaining]            (:items monkey)
                {:keys [operation test targets]} monkey
                worry                            (quot (operation current) 3)
                destination                      (if (zero? (mod worry test))
                                                   (first targets)
                                                   (second targets))]
            (recur i
                   (-> state
                       (assoc-in [i :items] (vec remaining))
                       (update-in [i :inspections] (fnil inc 0))
                       (update-in [destination :items] conj worry))))))
      state)))


(defn part-1
  "Solve part 1."
  [monkeys]
  (->> (iterate round monkeys)
       (drop 20)
       first
       (map :inspections)
       sort
       reverse
       (take 2)
       (apply *)))

(defn round-2
  "Perform one round of monkey business following part 2 rules, given the
  LCM of the test values for the monkeys."
  [lcm monkeys]
  (loop [i     0
         state monkeys]
    (if (< i (count monkeys))
      (let [monkey (nth state i)]
        (if (empty? (:items monkey))
          (recur (inc i) state)
          (let [[current & remaining]            (:items monkey)
                {:keys [operation test targets]} monkey
                worry                            (mod (operation current) lcm)
                destination                      (if (zero? (mod worry test))
                                                   (first targets)
                                                   (second targets))]
            (recur i
                   (-> state
                       (assoc-in [i :items] (vec remaining))
                       (update-in [i :inspections] (fnil inc 0))
                       (update-in [destination :items] conj worry))))))
      state)))

(defn part-2
  "Solve part 2."
  [monkeys]
  (let [lcm (apply * (map :test monkeys))]
    (->> (iterate (partial round-2 lcm) monkeys)
         (drop 10000)
         first
         (map :inspections)
         sort
         reverse
         (take 2)
         (apply *))))
