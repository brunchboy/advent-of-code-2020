(ns advent-of-code-2021.day-19
  "Solutions for day 19."
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (javax.vecmath Point3d)
           (javax.media.j3d Transform3D)))

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
