(ns advent-of-code-2022.day-20
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_20.txt")
       slurp
       edn/read-string))

(defn build-links
  "Much like the crab cups game from day 23 of 2020, We represent our
  message as a circular linked hash map. In my first attempt, Each key
  in the map was a number in the message, and each value is the number
  that follows it. I was foiled because, unlike in the crab cups game,
  the message values are *not* unique. So I had to change this so that
  the keys and values were tuples of [n i], where n is the number in
  the message, and i is an index ranging from 0 to the length of the
  message minus 1. Luckily the rest of the code did not need to be
  changed drastically to work with this approach."
  [message]
  (apply merge (for [i (range (count message))]
                 (let [j (mod (inc i) (count message))]
                   {[(message i) i] [(message j) j]}))))

(defn link-seq
  "In order to conveniently work with the circular message
  elements, this function returns a lazy sequence of the elements you
  will see moving clockwise starting with the tuple you supply.
  `links` is the circular linked hash map created by `build-links`."
  [links n]
  (lazy-seq (cons n (link-seq links (links n)))))

(defn extract
  "Removes the tuple t (which contains a message number and unique
  index) from the circular hash map."
  [links t len]
  (let [previous (nth (link-seq links t) (dec len))]
    (-> links
        (assoc previous (links t))
        (dissoc t))))

(defn insert
  "Inserts the tuple `t` after the tuple `predecessor` in the circular
  hash map."
  [links t predecessor]
  (let [next (links predecessor)]
    (-> links
        (assoc predecessor t)
        (assoc t next))))

(defn mix
  "Applies the mixing algorithm described in the problem statement to the
  message links, processing numbers in the order specified by the message."
  [message links]
  (reduce (fn [acc [n i]]
            (let [distance (mod n (dec (count message)))]
              (if (zero? distance)
                acc  ; No movement.
                (let [removed     (extract acc [n i] (count message))
                      destination (nth (link-seq removed (acc [n i])) (dec distance))]
                  (insert removed [n i] destination)))))
          links
          (partition 2 (interleave message (range)))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [mixed (link-seq (mix data (build-links data)) [0 (.indexOf data 0)])]
     (->> (map #(nth mixed (mod % (count data))) [1000 2000 3000])
          (map first)
          (apply +)))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [decrypted (mapv (partial * 811589153) data)
         mixed     (nth (iterate (partial mix decrypted) (build-links decrypted)) 10)
         mixed-seq (link-seq mixed [0 (.indexOf data 0)])]
     (->> (map #(nth mixed-seq (mod % (count data))) [1000 2000 3000])
          (map first)
          (apply +)))))

(def sample-input
  [1
   2
   -3
   3
   -2
   0
   4])
