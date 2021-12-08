(ns advent-of-code-2021.day-8
  "Day 8 solutions."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input "The puzzle input."
  (slurp (io/resource "2021/day_8.txt")))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([lines]
   (->> lines
        str/split-lines
        (map #(second (str/split % #" \| ")))
        (mapcat #(str/split % #"\s+"))
        (map count)
        (filter #{2 3 4 7})
        count)))

(defn find-mapping
  "Figure out the set of signals that correspond to each possible
  displayed digit. Returns a map from sets of characters (the active
  signal lines) to the numeric value of the digit they cause to be
  displayed."
  [all-digits]
  (let [all-digits (map set (str/split all-digits #"\s+"))
        one        (first (filter #(= 2 (count %)) all-digits))
        seven      (first (filter #(= 3 (count %)) all-digits))
        four       (first (filter #(= 4 (count %)) all-digits))
        eight      (first (filter #(= 7 (count %)) all-digits))
        nine       (first (filter #(and (= 6 (count %)) (= % (set/union % four))) all-digits))
        six        (first (filter #(and (= 6 (count %)) (= eight (set/union % one))) all-digits))
        five       (first (filter #(and (= 5 (count %)) (= % (set/intersection % six))) all-digits))
        three      (first (filter #(and (= 5 (count %)) (= seven (set/intersection % seven))) all-digits))
        two        (first (filter #(and (= 5 (count %)) (= 3 (count (set/intersection % five)))) all-digits))
        zero       (set/difference eight (set/intersection two five four))]
    {one   1
     two   2
     three 3
     four  4
     five  5
     six   6
     seven 7
     eight 8
     nine  9
     zero  0}))

(defn translate
  "Given a mapping from signal lines to digit, and a series of active
  signal line sets, return the number that the series represents."
  [mapping display]
  (let [digits (map set (str/split display #"\s+"))]
    (->> (map mapping digits)
         (apply str)
         Long/parseLong)))

(defn decode-line
  "Figure out the mapping of signal lines that is in effect for a line
  of the puzzle input, and then use that to determine the value being
  displayed, returning it as a number."
  [line]
  (let [[all-digits display] (str/split line #" \| ")
        result               (translate (find-mapping all-digits) display)]
    (println display "->" result)
    result))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([lines]
   (->> lines
        str/split-lines
        (map decode-line)
        (apply +))))
