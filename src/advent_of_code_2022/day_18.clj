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

(defn adjacent-cubes
  "Given a cube, returns the coordinates of the six cubes adjacent to
  each of its faces."
  [[x y z]]
  [[(dec x) y z]
        [x (dec y) z]
        [x y (dec z)]
        [(inc x) y z]
        [x (inc y) z]
        [x y (inc z)]])

(defn count-exposed-sides
  "Counts the faces of the specified cube that are not adjacent to any other cube."
  [cubes cube]
  (->> (adjacent-cubes cube)
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

(defn find-outside
  "Returns the set of cubes that surround the lava cubes, using a 3-d
  flood-fill algorithm bounded one cube further in each direction."
  [cubes]
  (let [transposed          (apply map list cubes)
        [max-x max-y max-z] (map #(inc (apply max %)) transposed)
        [min-x min-y min-z] (map #(dec (apply min %)) transposed)]
    (->> (iterate (fn [{:keys [outside newly-added] :as state}]
                    (-> state
                        (update :outside set/union (set newly-added))
                        ;; At each step we try growing any legal direction from the cubes that were
                        ;; most recently added. Any older cubes will already be surrounded.
                        (assoc :newly-added (->> (mapcat adjacent-cubes newly-added)
                                                 set
                                                 (remove (fn [cube]
                                                           (or (cubes cube)
                                                               (outside cube)
                                                               (let [[x y z] cube]
                                                                 (not (and (<= min-x x max-x)
                                                                           (<= min-y y max-y)
                                                                           (<= min-z z max-z)))))))))))
                  {:outside #{}  ; Start empty, and add flood fill seeds at all eight outside corners.
                   :newly-added [[min-x min-y min-z]
                                 [max-x min-y min-z]
                                 [min-x max-y min-z]
                                 [max-x max-y min-z]
                                 [min-x min-y max-z]
                                 [max-x min-y max-z]
                                 [min-x max-y max-z]
                                 [max-x max-y max-z]]})
         (drop-while #(seq (:newly-added %)))  ; Keep iterating until no new cubes get added.
         first
         :outside)))

(defn count-exposed-sides-2
  "Counts the faces of the specified cube that are adjacent to a outside cube."
  [outside cube]
  (->> (adjacent-cubes cube)
       (filter outside)
       count))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [cubes   (read-input data)
         outside (find-outside cubes)]
     (->> (map (partial count-exposed-sides-2 outside) cubes)
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
