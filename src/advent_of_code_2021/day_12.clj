(ns advent-of-code-2021.day-12
  "Solutions for day 12."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn read-edges
  "Turns the list of edges into a map from cave to sets of caves
  reachable from it."
  [lines]
  (reduce (fn [acc line]
            (let [[start end] (str/split line #"-")]
              (-> acc
                  (update start (fnil conj #{}) end)
                  (update end (fnil conj #{}) start))))
          {}
          (str/split-lines lines)))

(def input
  "The puzzle input, as a map from cave to sets of reachable caves."
  (-> (io/resource "2021/day_12.txt")
      slurp
      read-edges))

(defn eligible-next-caves
  "Determines the legal next moves given a path so far through the cave.
  You can always move to an upper-case cave, but can only move to a
  lower-case cave if you have not already visited it."
  [edges subpath]
  (->> (edges (last subpath))
       (filter (fn [candidate]
                 (or (= candidate (str/upper-case candidate))
                     (not-any? #(= % candidate) subpath))))))

(defn solve
  "Finds all paths through the cave, given the map of connectivity, and
  a subpath that has been traversed so far."
  [edges subpath]
  (if (= (last subpath) "end")
    [subpath]
    (filter identity (mapcat #(solve edges (conj subpath %)) (eligible-next-caves edges subpath)))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (solve data ["start"])))

(defn eligible-next-caves-2
  "A more complex determination of legal next moves given a path so far
  through the cave, and an indication of whether a small cave has yet
  been visited twice. You can always move to an upper-case cave, but
  can only move to a lower-case cave if you have not already visited
  it, or if you have not yet visited any other small cave twice (and
  you are not trying to move back to the start)."
  [edges subpath small-doubled?]
  (->> (edges (last subpath))
       (filter (fn [candidate]
                 (or (= candidate (str/upper-case candidate))
                     (not-any? #(= % candidate) subpath)
                     (and (not small-doubled?) (not= "start" candidate)))))))

(defn solve-2
  "Finds all paths through the cave, given the map of connectivity, a
  subpath that has been traversed so far, and an indication of whether
  we have yet visited any small cave twice."
  [edges subpath small-doubled?]
  (let [small-doubled? (or small-doubled?
                           (let [latest (last subpath)]
                             (and (= latest (str/lower-case latest))
                                  (some #{latest} (butlast subpath)))))]
    (if (= (last subpath) "end")
      [subpath]
      (filter identity (mapcat #(solve-2 edges (conj subpath %) small-doubled?)
                               (eligible-next-caves-2 edges subpath small-doubled?))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (solve-2 data ["start"] false)))
