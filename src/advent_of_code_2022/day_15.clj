(ns advent-of-code-2022.day-15
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

;;; "A poor man's interval tree, from http://clj-me.cgrand.net/2012/03/16/a-poor-mans-interval-tree/
;;; provided the basis for this slightly enhanced version I use in Beat Link Trigger. It will allow
;;; me to keep track of ranges more efficiently than sets of individual potential beacon locations.

(defn interval-lt
  "A partial order on intervals and points, where an interval is defined
  by the vector [from to) (notation I recall meaning it includes the
  lower bound but excludes the upper bound), and either can be `nil`
  to indicate negative or positive infinity. A single point at `n` is
  represented by `[n n]`."
  [[a b] [c _]]
  (boolean (and b c
                (if (= a b)
                  (neg? (compare b c))
                  (<= (compare b c) 0)))))

(def empty-interval-map
  "An interval map with no content."
  (sorted-map-by interval-lt [nil nil] #{}))

(defn- isplit-at
  "Splits the interval map at the specified value, unless it already has
  a boundary there."
  [interval-map x]
  (if x
    (let [[[a b :as k] vs] (find interval-map [x x])]
      (if (or (= a x) (= b x))
        interval-map
        (-> interval-map (dissoc k) (assoc [a x] vs [x b] vs))))
    interval-map))

(defn matching-subsequence
  "Extracts the sequence of key, value pairs from the interval map which
  cover the supplied range (either end of which can be `nil`, meaning
  from the beginning or to the end)."
  [interval-map from to]
  (cond
    (and from to)
    (subseq interval-map >= [from from] < [to to])
    from
    (subseq interval-map >= [from from])
    to
    (subseq interval-map < [to to])
    :else
    interval-map))

(defn- ialter
  "Applies the specified function and arguments to all intervals that
  fall within the specified range in the interval map, splitting it at
  each end if necessary so that the exact desired range can be
  affected."
  [interval-map from to f & args]
  (let [interval-map (-> interval-map (isplit-at from) (isplit-at to))
        kvs          (for [[r vs] (matching-subsequence interval-map from to)]
                       [r (apply f vs args)])]
    (into interval-map kvs)))

(defn iassoc
  "Add a value to the specified range in an interval map."
  [interval-map from to v]
  (ialter interval-map from to conj v))

(defn idissoc
  "Remove a value from the specified range in an interval map. If you
  are going to be using this function much, it might be worth adding
  code to consolidate ranges that are no longer distinct."
  [interval-map from to v]
  (ialter interval-map from to disj v))

(defn iget
  "Find the values that are associated with a point or interval within
  the interval map. Calling with a single number will look up the
  values associated with just that point; calling with two arguments,
  or with an interval vector, will return the values associated with
  any interval that overlaps the supplied interval."
  ([interval-map x]
   (if (vector? x)
     (let [[from to] x]
       (iget interval-map from to))
     (iget interval-map [x x])))
  ([interval-map from to]
   (reduce (fn [result [_ vs]]
             (clojure.set/union result vs))
           #{}
           (take-while (fn [[[start]]] (< (or start (dec to)) to)) (matching-subsequence interval-map from nil)))))

;;; End of interval tree code.


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

;; This approach worked, slowly, for the sample data, but was clearly not going to cut it
;; for the real problem.

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
  (reduce (fn [acc [sensor-cell beacon-cell]]
            (set/union acc #{sensor-cell beacon-cell}))
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

;; This approach solved the sample data instantly, and so it looked
;; like it would be fast enough for the real data, but no...

(defn count-interval-cells
  "Counts how many filled cells are represented by an interval map."
  [intervals]
  (reduce (fn [acc [[start end] value]]
            (if (get value true)
              (+ acc (- end start))
              acc))
          0
          intervals))

(defn count-beacons-on-row
  "Counts how many beacons are present on a row."
  [sensors row]
  (->> sensors
       (map second)
       (filter (fn [[_x y]] (= y row)))
       set
       count))

(defn build-coverage-intervals
  "Builds an interval tree representing all the cells that can be seen by
  any sensor in the specified row."
  [sensors y]
  (reduce (fn [intervals [sensor-cell beacon-cell]]
            (let [max-distance        (manhattan-distance sensor-cell beacon-cell)
                  [sensor-x sensor-y] sensor-cell
                  slack               (- max-distance (Math/abs (- y sensor-y)))]
              (if (neg? slack)
                intervals
                (iassoc intervals (- sensor-x slack) (+ sensor-x slack 1) true))))
          empty-interval-map
          sensors))

(defn part-1
  "Solve part 1."
  ([y]
   (part-1 input y))
  ([data y]
   (let [sensors (read-input data)]
     (- (count-interval-cells (build-coverage-intervals sensors y)) (count-beacons-on-row sensors y)))))


;; This approach turned out to be way too slow, so see below...
#_(defn build-all-coverage-intervals
  [sensors]
  (reduce (fn [intervals [sensor-cell beacon-cell]]
            (let [max-distance (manhattan-distance sensor-cell beacon-cell)
                  [_ sensor-y] sensor-cell]
              (reduce (fn [intervals y]
                        (update intervals y #(or % (build-coverage-intervals sensors y))))
                      intervals
                      (range (- sensor-y max-distance) (+ sensor-y max-distance 1)))))
          {}
          sensors))

#_(defn find-gap
  "Finds an entry in the middle an interval tree whose value is not true,
  and returns its starting coordinate."
  [intervals]
  (->> intervals
       (filter (fn [[[start end] val-set]]
                 (and start end (empty? val-set))))
       first
       first
       first))

#_(defn find-distress-beacon
  "Returns the x and y coordinate where the distress beacon can be found."
  [sensors max-y]
  (let [all-intervals (build-all-coverage-intervals sensors)]
    (->> (for [y (range (inc max-y))]
           (when-let [intervals (all-intervals y)]
             (when-let [x (find-gap intervals)]
               [x y])))
         (filter identity)
         first)))

;; This is the approach that actually worked.
;; Just check the cells that are exactly beyond the range of each sensor to save a ton of time.

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
