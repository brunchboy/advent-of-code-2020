(ns advent-of-code-2021.day-19
  "Solutions for day 19."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.math.combinatorics :as combo])
  (:import (javax.vecmath Point3d)
           (javax.media.j3d Transform3D)))

(defn parse-probe
  "Parses a line of probe coordinates into a corresponding tuple."
  [probe]
  (->> (str/split probe #",")
       (mapv #(Long/parseLong %))))

(defn parse-probes
  "Builds a set of all the coordinates of probes seen by a scanner.
  Wrap them as a map entry so we can keep track of other things about
  the scanner, such as its offset and rotation, and normalized
  versions of its probes (as seen from the perspective of scanner 0)."
  [probes]
  {:raw-probes (set (map parse-probe probes))})

(defn read-input
  "Read the problem input format into a list of sensor information."
  [path]
  (->> (io/resource path)
       slurp
       str/split-lines
       (partition-by str/blank?)
       (map rest)
       (filter seq)
       (map parse-probes)))

(def input
  "The sets of probes reported by each sensor, the problem input."
  (read-input "2021/day_19.txt"))

(defn rotate
  "Given a `Transform3D` object representing one of the 24 rotations we
  are considering, and a vector of longs representing a point we are
  considering, applies the rotation to the vector (converting to
  doubles first, then rounding back to longs)."
  [transform point-vec]
  (let [[x y z] (map double point-vec)
        point   (Point3d. x y z)]
    (.transform transform point)
    [(Math/round (.x point)) (Math/round (.y point)) (Math/round (.z point))]))

(def all-rotations
  "A vector of `Transform3D` objects representing all of the rotations
  that the problem statement requires us to consider, that is, the
  unique combinations of right-angle rotations around each of the
  axes. Rather than trying to figure out the exact 24 combinations by
  hand, generate all 64 combinations by rotating to all four positions
  around each axis, then filter out any which end up being redundant."
  (let [candidates (for [x (range 4)
                         y (range 4)
                         z (range 4)]
                     [x y z])]
    (loop [result    []
           seen      #{}
           remaining candidates]
      (if (empty? remaining)
        result
        (let [[x y z] (first remaining)
              rot-x (Transform3D.)
              rot-y (Transform3D.)
              rot-z (Transform3D.)]
          (.rotX rot-x (* Math/PI 0.5 x))
          (.rotY rot-y (* Math/PI 0.5 y))
          (.rotZ rot-z (* Math/PI 0.5 z))
          (.mul rot-x rot-y)
          (.mul rot-x rot-z)
          (let [probe (rotate rot-x [1 2 3])]
            (if (seen probe)
              (recur result seen (rest remaining))
              (recur (conj result rot-x)
                     (conj seen probe)
                     (rest remaining)))))))))

(defn find-offset
  "Given two points, calculate the offset which, when added to the
  second, yields the first."
  [anchor-point candidate-point]
  (mapv - anchor-point candidate-point))

(defn translate-sensor
  "Apply an offset to the sensor's set of probes, and record it."
  [sensor offset]
  (-> sensor
      (assoc :normalized-probes (set (map #(mapv + offset %) (:normalized-probes sensor)))
             :offset offset)))

(defn enough-overlap?
  "See if there are enough matching probes in the normalized probe sets
  of the supplied sensors to consider a sensor overlap to have been
  found."
  [anchor candidate]
  (>= (count (set/intersection (:normalized-probes anchor) (:normalized-probes candidate))) 12))

(defn resolve-if-rotated-sensors-overlap
  "Given an anchor sensor which has been successfully normalized, see if
  there is a translation of a candidate rotated sensor which has
  enough overlap for it to be considered successfully normalized as
  well. If so, return the candidate with that translation applied to
  its normalized probe set."
  [anchor candidate]
  (first (filter (fn [translated-sensor] (enough-overlap? anchor translated-sensor))
                 (map (partial translate-sensor candidate)
                      (map (partial apply find-offset)
                           (combo/cartesian-product (:normalized-probes anchor)
                                                    (:normalized-probes candidate)))))))

(defn resolve-if-sensors-overlap
  "Given a candidate sensor, try all possible rotations to see if any of
  them can yield a successful overlap (with enough matching probes)
  under some translation. If so, return the sensor with the resolved
  normalized probes, rotation, and offset information."
  [anchor candidate]
  (loop [rotations all-rotations]
    (when (seq rotations)
      (let [rotated (set (map (partial rotate (first rotations)) (:raw-probes candidate)))]
        (or (resolve-if-rotated-sensors-overlap
             anchor (merge candidate {:normalized-probes rotated
                                      :rotation          (first rotations)}))
            (recur (rest rotations)))))))

(defn find-sensor-overlapping-anchor
  "Given a successfully resolved anchor sensor (with normalized probes),
  try to find a sensor which can be rotated and translated to
  successfully overlap with it. If successful, return a tuple of that
  newly-normalized sensor, along with a list of the ones which were
  not yet resolved."
  [anchor candidates]
  (loop [rejected '()
         candidates candidates]
    (when (seq candidates)
      (let [candidate (first candidates)]
        (if-let [resolved (resolve-if-sensors-overlap anchor candidate)]
          [resolved (concat rejected (rest candidates))]
          (recur (conj rejected candidate) (rest candidates)))))))

(defn find-overlapping-sensor
  "Given a list of resolved sensors (in known orientations and
  positions, with normalized probe coordinates), find one that can now
  be resolved by rotating and translating it."
  [known candidates]
  (loop [anchors known]
    (if (empty? anchors)
      (throw (Exception. "No overlapping sensor found."))
      (or (find-sensor-overlapping-anchor (first anchors) candidates)
          (recur (rest anchors))))))

(defn normalize-sensors
  [sensors]
  (let [origin (first sensors)]
    (loop [known      [(merge origin {:offset            [0 0 0]
                                      :rotation          (Transform3D.)
                                      :normalized-probes (:raw-probes origin)})]
           candidates (rest sensors)]
      (if (empty? candidates)
        known
        (let [[found remaining] (find-overlapping-sensor known candidates)]
          (println (inc (count known)) "sensors normalized and" (count remaining) "remain.")
          (recur (conj known found)
                 remaining))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([sensors]
   (->> sensors
        normalize-sensors
        (map :normalized-probes)
        (apply set/union)
        count)))

(defn manhattan-distance
  [point-1 point-2]
  (apply + (map #(Math/abs %) (find-offset point-1 point-2))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([sensors]
   (let [offsets (->> sensors
                      normalize-sensors
                      (map :offset))]

     (->> (combo/combinations offsets 2)
          (map (partial apply manhattan-distance))
          (apply max)))))
