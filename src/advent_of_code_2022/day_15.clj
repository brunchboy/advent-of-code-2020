(ns advent-of-code-2022.day-15
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

;; For historical attempts to solve part 2 using interval trees, which sped up part 1 a lot,
;; but not enough to make part 2 practical using them, see the git history.

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_15.txt")
       slurp))

(defn read-input
  "Parses the input into tuples of sensor and beacon coordinates."
  [data]
  (->> (str/split-lines data)
       (map (fn [line]
              (->> (re-seq #"-?\d+" line)
                   (map #(Long/parseLong %))
                   (partition 2))))))

(defn manhattan-distance
  "Calculate the manhattan distance between two points."
  [[x1 y1] [x2 y2]]
  (+ (Math/abs (- x1 x2)) (Math/abs (- y1 y2))))

(defn cells-too-close-set
  [[sensor-cell beacon-cell] y]
  (let [sensor-x     (first sensor-cell)
        max-distance (manhattan-distance sensor-cell beacon-cell)
        slack        (- max-distance (manhattan-distance sensor-cell [sensor-x y]))]
    (when-not (neg? slack)
      (set (for [x (range (- sensor-x slack) (inc (+ sensor-x slack)))]
             [x y])))))

(defn occupied-cells-set
  [sensors]
  (reduce (fn [acc [_sensor-cell beacon-cell]]
            (set/union acc #{beacon-cell}))
          #{}
          sensors))

(defn count-impossible-cells-set
  [sensors y]
  (let [occupied   (occupied-cells-set sensors)
        candidates (reduce (fn [acc sensor]
                             (set/union acc (cells-too-close-set sensor y)))
                           #{}
                           sensors)]
    (count (set/difference candidates occupied))))

(defn part-1
  "Solve part 1."
  ([y]
   (part-1 input y))
  ([data y]
   (let [sensors (read-input data)]
     (count-impossible-cells-set sensors y))))


(defn just-beyond-sensor-range
  "Return the set of cells which are just beyond the sensor range, but
  within the bounds specified by the problem."
  [sensor-cell beacon-cell max-xy]
  (let [max-distance        (manhattan-distance sensor-cell beacon-cell)
        [sensor-x sensor-y] sensor-cell]
    (->> (for [dy (range (+ max-distance 2))]
           (let [slack (- (inc max-distance) dy)]
             (concat
              (when (<= 0 (- sensor-y dy) max-xy)
                (concat
                 (when (<= 0 (- sensor-x slack) max-xy)
                   [[(- sensor-x slack) (- sensor-y dy)]])
                 (when (and (pos? slack)
                            (<= 0 (+ sensor-x slack) max-xy))
                   [[(+ sensor-x slack) (- sensor-y dy)]])))
              (when (and (pos? dy)
                         (<= 0 (+ sensor-y dy) max-xy))
                (concat
                 (when (<= 0 (- sensor-x slack) max-xy)
                   [[(- sensor-x slack) (+ sensor-y dy)]])
                 (when (and (pos? slack)
                            (<= 0 (+ sensor-x slack) max-xy))
                   [[(+ sensor-x slack) (+ sensor-y dy)]]))))))
         (apply concat)
         set)))

(defn invisible?
  "Checks whether a point cannot be seen by any sensor."
  [sensors point]
  (loop [[[sensor-cell beacon-cell] & remaining] sensors]
    (let [range (manhattan-distance sensor-cell beacon-cell)
          distance (manhattan-distance sensor-cell point)]
      (when (> distance range)
        (or (empty? remaining)
            (recur remaining))))))

(defn find-distress-beacon
  "Finds the only point that can hold the distress beacon."
  [sensors max-xy]
  (loop [tried                                   #{}
         [[sensor-cell beacon-cell] & remaining] sensors]
    (let [candidates (set/difference (just-beyond-sensor-range sensor-cell beacon-cell max-xy) tried)]
         (or (first (filter (partial invisible? sensors) candidates))
             (when (seq remaining)
               (recur (set/union tried candidates)
                      remaining))))))

(defn part-2
  "Solve part 2."
  ([max-xy]
   (part-2 input max-xy))
  ([data max-xy]
   (let [sensors (read-input data)
         [x y]   (find-distress-beacon sensors max-xy)]
     (+ (* x 4000000) y))))

(def sample-input
  "Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3")
