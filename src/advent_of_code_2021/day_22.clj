(ns advent-of-code-2021.day-22
  "Solutions for day 22."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn parse-line
  "Given a string describing a boot operation, build a tuple of the
  action string (on or off) and six numbers bounding the rectangular
  prism, i.e. [action x-min x-max y-min y-max z-min z-max]."
  [line]
  (let [[action & bounds] (rest (re-matches #"(\w+) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)"
                                            line))]
    (vec (concat [action] (map #(Long/parseLong %) bounds)))))

(defn read-input
  "Read puzzle input from the specified resource path."
  [path]
  (->> (io/resource path)
       slurp
       str/split-lines
       (map parse-line)))

(def input
  "The puzzle input."
  (read-input "2021/day_22.txt"))

(defn sanitize-input-line
  "If this line is entirely outside the initialization procedure area,
  discard it. Otherwise clip it to fit within the area."
  [[action x-min x-max y-min y-max z-min z-max]]
  (when (and (<= x-min 50) (<= y-min 50) (<= z-min 50)
             (>= x-max -50) (>= y-max -50) (>= z-max -50))
    [action (max x-min -50) (min x-max 50) (max y-min -50) (min y-max 50) (max z-min -50) (min z-max 50)]))

(defn sanitize-input
  "Get rid of lines that are entirely outside the initialization procedure area,
  and clip others to fit within it."
  [steps]
  (->> steps
      (map sanitize-input-line)
      (filter identity)))

(defn boot-step
  "Run one step of the boot sequence, adding or removing cubes as
  appropriate."
  [cubes [action x-min x-max y-min y-max z-min z-max]]
  (reduce (fn [acc cube]
            (case action
              "on"  (conj acc cube)
              "off" (disj acc cube)))
          cubes
          (for [x (range x-min (inc x-max))
                y (range y-min (inc y-max))
                z (range z-min (inc z-max))]
            [x y z])))

(defn boot
  "Run the boot sequence, returning the set of cubes which are on at the
  end of it."
  [steps]
  (reduce boot-step
          #{}
          steps))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([steps]
   (count (boot (sanitize-input steps)))))
