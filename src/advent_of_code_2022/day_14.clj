(ns advent-of-code-2022.day-14
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_14.txt")
       slurp))

(defn read-wall
  "Read one wall specification from the puzzle input, adding it to the
  set of cave points, and keeping track of the deepest."
  [state [start end]]
  (let [[start-x start-y] (map #(Long/parseLong %) (str/split start #","))
        [end-x end-y] (map #(Long/parseLong %) (str/split end #","))
        [start-x end-x] (sort [start-x end-x])
        [start-y end-y] (sort [start-y end-y])]
    (-> state
        (update :cave set/union (set (if (= start-y end-y)
                                       (for [x (range start-x (inc end-x))]
                                         [x end-y])
                                       (for [y (range start-y (inc end-y))]
                                         [end-x y]))))
        (update :depth max end-y))))

(defn read-line
  "Read a line of puzzle input, adding the new walls to the cave, keeping
  track of the deepest."
  [state line]
  (reduce (fn [acc wall]
            (read-wall acc wall))
          state
          (partition 2 1 (str/split line #"\s+->\s+"))))

(defn read-input
  "Read the puzzle input, returning the set of cave walls and the deepth
  of the deepest cave wall."
  [data]
  (reduce (fn [acc line]
            (read-line acc line))
          {:cave    #{}
           :depth   0
           :filling true}
          (str/split-lines data)))

(defn add-sand
  "Adds a unit of sand to the cave, flagging whether it has flowed into
  the void."
  [{:keys [cave depth] :as state}]
  (loop [x 500
         y 0]
    (if (> y depth)
      (dissoc state :filling)
      (if-not (cave [x (inc y)])
        (recur x (inc y))
        (if-not (cave [(dec x) (inc y)])
          (recur (dec x) (inc y))
          (if-not (cave [(inc x) (inc y)])
            (recur (inc x) (inc y))
            (update state :cave conj [x y])))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (let [stages (iterate add-sand (read-input data))]
     (dec (count (take-while :filling stages))))))

(defn add-sand-2
  "Adds a unit of sand to the cave for part 2, flagging whether it has
  backed up to the starting point."
  [{:keys [cave depth] :as state}]
  (if (cave [500 0])
    (dissoc state :filling)
    (loop [x 500
           y 0]
      (if (> y depth)
        (update state :cave conj [x y])
        (if-not (cave [x (inc y)])
          (recur x (inc y))
          (if-not (cave [(dec x) (inc y)])
            (recur (dec x) (inc y))
            (if-not (cave [(inc x) (inc y)])
              (recur (inc x) (inc y))
              (update state :cave conj [x y]))))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [stages (iterate add-sand-2 (read-input data))]
     (dec (count (take-while :filling stages))))))

(def sample-input
  "498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9")
