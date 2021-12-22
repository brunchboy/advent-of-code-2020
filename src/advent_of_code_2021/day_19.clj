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
  "Builds a set of all the coordinates of probes seen by a scanner."
  [probes]
  (set (map parse-probe probes)))

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
  [anchor-point candidate-point]
  (mapv - anchor-point candidate-point))

(defn translate-sensor
  [sensor offset]
  (set (map #(mapv + offset %) sensor)))

(defn enough-overlap?
  [anchor candidate]
  (>= (count (set/intersection anchor candidate)) 12))

(defn resolve-if-rotated-sensors-overlap
  [anchor candidate]
  (first (filter (fn [translated-sensor] (enough-overlap? anchor translated-sensor))
                 (map (partial translate-sensor candidate)
                      (map (partial apply find-offset) (combo/cartesian-product anchor candidate))))))

(defn resolve-if-sensors-overlap
  [anchor candidate]
  (loop [rotations all-rotations]
    (when (seq rotations)
      (let [rotated (set (map (partial rotate (first rotations)) candidate))]
        (or (resolve-if-rotated-sensors-overlap anchor rotated)
            (recur (rest rotations)))))))

(defn find-sensor-overlapping-anchor
  [anchor candidates]
  (loop [rejected '()
         candidates candidates]
    (when (seq candidates)
      (let [candidate (first candidates)]
        (if-let [resolved (resolve-if-sensors-overlap anchor candidate)]
          [resolved (concat rejected (rest candidates))]
          (recur (conj rejected candidate) (rest candidates)))))))

(defn find-overlapping-sensor
  [known candidates]
  (loop [anchors known]
    (if (empty? anchors)
      (throw (Exception. "No overlapping sensor found."))
      (or (find-sensor-overlapping-anchor (first anchors) candidates)
          (recur (rest anchors))))))

(defn normalize-sensors
  [sensors]
  (loop [known      (take 1 sensors)
         candidates (rest sensors)]
    (if (empty? candidates)
      known
      (let [[found remaining] (find-overlapping-sensor known candidates)]
        (println (inc (count known)) "sensors normalized and" (count remaining) "remain.")
        (recur (conj known found)
               remaining)))))
