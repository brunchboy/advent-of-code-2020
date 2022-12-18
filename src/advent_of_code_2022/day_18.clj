(ns advent-of-code-2022.day-18
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_18.txt")
       slurp
       edn/read-string))

(defn read-input
  "Turns the raw input vector into a set of x,y,z tuples."
  [data]
  (->> data
       (partition 3)
       (map vec)
       set))

(defn count-exposed-sides
  "Counts the faces of the specified cube that are not adjacent to any other cube."
  [cubes [x y z]]
  (->> [[(dec x) y z]
        [x (dec y) z]
        [x y (dec z)]
        [(inc x) y z]
        [x (inc y) z]
        [x y (inc z)]]
       (remove cubes)
       count))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [cubes (read-input data)]
     (->> (map (partial count-exposed-sides cubes) cubes)
          (apply +)))))

(defn find-boundaries
  "Returns the set of cubes that surround the lava cubes, using a 3-d
  flood-fill algorithm bounded one cube away in each direction."
  ([cubes]
   (let [transposed          (apply map list cubes)
         [max-x max-y max-z] (map #(apply max %) transposed)
         [min-x min-y min-z] (map #(apply min %) transposed)
         x                   (dec min-x)
         y                   (dec min-y)
         z                   (dec min-z)]
     (find-boundaries cubes #{[x y z]} max-x max-y max-z min-x min-y min-z x y z)))
  ([cubes boundaries max-x max-y max-z min-x min-y min-z x y z]
   (as-> boundaries $
     (if (and (>= x min-x)
              (not (or (cubes [(dec x) y z]) ($ [(dec x) y z]))))
       (find-boundaries cubes (conj $ [(dec x) y z]) max-x max-y max-z min-x min-y min-z (dec x) y z)
       $)
     (if (and (<= x max-x)
              (not (or (cubes [(inc x) y z]) ($ [(inc x) y z]))))
       (find-boundaries cubes (conj $ [(inc x) y z]) max-x max-y max-z min-x min-y min-z (inc x) y z)
       $)
     (if (and (>= y min-y)
              (not (or (cubes [x (dec y) z]) (boundaries [x (dec y) z]))))
       (find-boundaries cubes (conj $ [x (dec y) z]) max-x max-y max-z min-x min-y min-z x (dec y) z)
       $)
     (if (and (<= y max-y)
              (not (or (cubes [x (inc y) z]) (boundaries [x (inc y) z]))))
       (find-boundaries cubes (conj $ [x (inc y) z]) max-x max-y max-z min-x min-y min-z x (inc y) z)
       $)
     (if (and (>= z min-z)
          (not (or (cubes [x y (dec z)]) (boundaries [x y (dec z)]))))
       (find-boundaries cubes (conj $ [x y (dec z)]) max-x max-y max-z min-x min-y min-z x y (dec z))
       $)
     (if (and (<= z max-z)
              (not (or (cubes [x y (inc z)]) (boundaries [x y (inc z)]))))
       (find-boundaries cubes (conj $ [x y (inc z)]) max-x max-y max-z min-x min-y min-z x y (inc z))
       $))))

(defn count-exposed-sides-2
  "Counts the faces of the specified cube that are adjacent to a boundary cube."
  [boundaries [x y z]]
  (->> [[(dec x) y z]
        [x (dec y) z]
        [x y (dec z)]
        [(inc x) y z]
        [x (inc y) z]
        [x y (inc z)]]
       (filter boundaries)
       count))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [cubes      (read-input data)
         boundaries (find-boundaries cubes)]
     (->> (map (partial count-exposed-sides-2 boundaries) cubes)
          (apply +)))))

(def sample-input
  [2,2,2
   1,2,2
   3,2,2
   2,1,2
   2,3,2
   2,2,1
   2,2,3
   2,2,4
   2,2,6
   1,2,5
   3,2,5
   2,1,5
   2,3,5])
