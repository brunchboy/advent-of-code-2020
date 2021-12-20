(ns advent-of-code-2021.day-20
  "Solutions for day 20."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]))

(def enhancement
  "The so-called enhancement algorithm from the puzzle input."
  (-> (io/resource "2021/day_20_enhancement.txt")
                     slurp))

(defn read-image
  "Build a set containing all points which are turned on in the lines of
  an image formatted as described in the problem statement."
  [lines]
  (->> (for [x (range (count (first lines)))
             y (range (count lines))]
         (when (= (get-in lines [y x]) \#)
           [x y]))
       (filter identity)
       set))

(def image
  "The set of points that are on in the puzzle input."
  (-> (io/resource "2021/day_20_image.txt")
               slurp
               str/split-lines
               read-image))

(defn include-neighbors
  "This is no longer used; my first, failed attempt at solution
  considered only the neighbors of active points. That worked for the
  sample enhancement algorithm, and I even noticed that the problem
  would not make sense if the algorithm turned on a point which had no
  active neigbors, but then didn't notice tha my actual puzzle input
  used precisely such a pathological algorithm, so I wasted time
  wondering why I was getting the wrong answer, then even longer
  trying to figure out how to cope with the crazy situation where an
  infinite number of points are active on alternating iterations."
  [[x y]]
  (set (for [dy (range 3)
             dx (range 3)]
         [(+ x dx -1) (+ y dy -1)])))

(defn candidate-points
  "Again no longer used; built up a set of points that were close enough
  to existing active points that they needed to be considered. That
  only works when the enhancement algorithm leaves points with zero
  neighbors inactive."
  [image]
  (reduce (fn [acc point]
            (set/union acc (include-neighbors point)))
          #{}
          image))

(defn bounds
  "For the second attempt at solving this, I iterate over all points
  that are covered by the image, and then an additional border. This
  returns the bounds of all active points in the image to facilitate
  that."
  [image]
  (reduce (fn [[min-x max-x min-y max-y] [x y]]
            [(min x min-x) (max x max-x) (min y min-y) (max y max-y)])
          [0 0 0 0]
          image))


(defn enhancement-index
  "Calculate the index to look up in the enhancement algorithm string,
  forming a binary number based on the nine cells adjacent to and
  including the specified cell."
  [image [x y]]
  (let [binary-str (->> (for [dy (range 3)
                              dx (range 3)]
                          (if (image [(+ x dx -1) (+ y dy -1)]) "1" "0"))
                        (apply str))]
    (Long/parseLong binary-str 2)))

(defn step-twice
  "The only way I could think of solving this was to always perform
  pairs of steps, realizing that cells far enough away from the active
  portion of the diagram will turn back off again on the second
  iteration. So consider three extra cells in all directions during
  the turn-on stage, and two extra cells when they are turning back
  off. Thankfully, even in part 2, we performed an even number of
  steps."
  [enhancement image]
  (let [[min-x max-x min-y max-y] (bounds image)
        step-1                    (->> (for [x (range (- min-x 3) (+ max-x 4))
                                             y (range (- min-y 3) (+ max-y 4))]
                                         [x y])
                                       (filter (fn [point]
                                                 (= (nth enhancement (enhancement-index image point)) \#)))
                                       set)]
    (->> (for [x (range (- min-x 2) (+ max-x 3))
               y (range (- min-y 2) (+ max-y 3))]
           [x y])
         (filter (fn [point]
                   (= (nth enhancement (enhancement-index step-1 point)) \#)))
         set)))
