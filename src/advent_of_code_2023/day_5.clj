(ns advent-of-code-2023.day-5
  (:require [clojure.java.io :as io]
            [instaparse.core :as insta]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2023/day_5.txt")
       slurp))

(def sample-input
  "The sample data for testing a solution."
  "seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4")

(defn apply-almanac-entry
  "Sees whether a candidate number falls within the source range for an
  almanac map entry. If so, translates it to the corresponding
  destination range, otherwise returns `nil`."
  [n [dest source length]]
  (when (<= source n (+ source (dec length)))
    (+ dest (- n source))))

(defn pass-through-map
  "Translates an incoming value through one of the ranged maps present in
the almanac."
  [n m]
  (or (some (partial apply-almanac-entry n) (:entries m))
      n))

(def instaparse-almanac
  "Create a parser for the almanac format."
  (insta/parser (io/resource "2023/day_5.bnf")))

(defn eval-map
  "Converts a parsed map structure into a Clojure map with parsed numbers for its entries."
  [m]
  {:name (second m)
   :entries (mapv #(mapv parse-long (rest %)) (drop 2 m))})

(defn find-lowest-location-seed
  "Find the seed whose location value is smallest."
  [seeds maps]
  (reduce min (map #(reduce pass-through-map % maps) seeds)))

(defn part-1
  "Solve part 1 of the problem"
  ([]
   (part-1 input))
  ([data]
   (let [parsed (instaparse-almanac data)
         seeds  (map parse-long (rest (second parsed)))
         maps   (map eval-map (drop 2 parsed))]
     (find-lowest-location-seed seeds maps))))

(defn in-range?
  "Check whether a number falls within any of the supplied seed ranges."
  [n ranges]
  (some (fn [[start length]] (<= start n (dec (+ start length)))) ranges))

(defn apply-almanac-entry-backwards
  "Sees whether a candidate number falls within the destination range for an
  almanac map entry. If so, translates it to the corresponding source
  range, otherwise returns `nil`."
  [n [dest source length]]
  (when (<= dest n (+ dest (dec length)))
    (+ source (- n dest))))

(defn pass-through-map-backwards
  "Translates an value backwards through one of the ranged maps present in
  the almanac."
  [n m]
  (or (some (partial apply-almanac-entry-backwards n) (:entries m))
      n))

(defn part-2
  "Solve part 2 of the problem"
  ([]
   (part-2 input))
  ([data]
   (let [parsed (instaparse-almanac data)
         ranges (partition 2 (map parse-long (rest (second parsed))))
         maps   (reverse (map eval-map (drop 2 parsed)))]
     (some (fn [location]
             (when (zero? (mod location 100000)) (println location))
              (when (in-range? (reduce pass-through-map-backwards location maps) ranges)
                location))
           (range)))))
