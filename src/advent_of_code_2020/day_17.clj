(ns advent-of-code-2020.day-17
  "Solutions for day 17."
  (:require [clojure.string :as str]))

(def input
  "The initial active cubes (puzzle input)."
  (str/split-lines ".#######
#######.
###.###.
#....###
.#..##..
#.#.###.
###..###
.#.#.##."))

(defn process-row
  "Converts a row of the input diagram into a list of the coordinates of
  the active cells."
  [y row]
  (->> (map-indexed (fn [x c]
                      (when (= c \#) [x y 0]))
                    row)
       (filter identity)))

(defn initial-state
  "Builds the list of all active cells at the start of the problem."
  ([]
   (initial-state input))
  ([rows]
   (set (apply concat (map-indexed process-row rows)))))

(defn cube-bounds
  "Finds the lowest and hightest values along each axis for all active
  cells, allowing me to scan the cube of potential next generation
  calls. In retrospect it would probably be more efficient to just
  scan the neighborhood of each active cell in the current generation
  instead; there will be some overlap and duplicates to filter out
  when building the next generation set, but a lot of empty space
  could be completely skipped."
  [cubes]
  (let [axes (apply map vector cubes)]
    {:max (map #(apply max %) axes)
     :min (map #(apply min %) axes)}))

(defn count-neighbors
  "Count the active cells adjacent to the specified cell."
  [cubes [x y z :as cell]]
  (apply +
         (for [nx (range (dec x) (+ x 2))
               ny (range (dec y) (+ y 2))
               nz (range (dec z) (+ z 2))]
           (let [neighbor [nx ny nz]]
             (if (and (not= cell neighbor)
                      (cubes neighbor))
               1
               0)))))

(defn step
  "Apply the rules for computing the next generation from the current
  state, and return the resulting set of active cells."
  [cubes]
  (let [bounds (cube-bounds cubes)]
    (->> (for [x (range (dec (first (:min bounds))) (+ (first (:max bounds)) 2))
               y (range (dec (second (:min bounds))) (+ (second (:max bounds)) 2))
               z (range (dec (nth (:min bounds) 2)) (+ (nth (:max bounds) 2) 2))]
           (let [cell       [x y z]
                 neighbors (count-neighbors cubes cell)]
             (if (cubes cell)
               (when (#{2 3} neighbors) cell)
               (when (= 3 neighbors) cell))))
         (filter identity)
         set)))

(defn part-1
  "Solve part 1 of the problem, returning the sixth generation from the
  starting state. Optionally allows the initial state diagram to be
  passed as an argument for unit testing."
  ([]
   (part-1 input))
  ([rows]
   (count (nth (iterate step (initial-state rows)) 6))))

;;; And now to redo it all with one more dimension for part 2, sigh,
;;; if only I had generalized this to begin with! Although it turns
;;; out I could reuse more than I expected, and this went quickly.

(defn initial-state-2
  "As before, but with an extra dimension. We can just tack a zero onto
  the end of each cell's coordinate vector."
  ([]
   (initial-state-2 input))
  ([rows]
   (let [cubes (initial-state rows)]
     (set (map (fn [cube] (conj cube 0)) cubes)))))

(defn count-neighbors-2
  "Just adds an extra dimension to `count-neighbors`. I am sure there is
  a way to parameterize this by dimension, but I wanted to power ahead
  to a solution rather than sorting that out."
  [cubes [x y z w :as cell]]
  (apply +
         (for [nx (range (dec x) (+ x 2))
               ny (range (dec y) (+ y 2))
               nz (range (dec z) (+ z 2))
               nw (range (dec w) (+ w 2))]
           (let [neighbor [nx ny nz nw]]
             (if (and (not= cell neighbor)
                      (cubes neighbor))
               1
               0)))))

(defn step-2
  "Apply the part 2 rules for computing the next generation from the
  current state, and return the resulting set of active cells. Again,
  just adds an extra dimension to `step`, and there is probably a way
  to parameterize this by dimension, but I was going for speed, not
  brilliance here."
  [cubes]
  (let [bounds (cube-bounds cubes)]
    (->> (for [x (range (dec (first (:min bounds))) (+ (first (:max bounds)) 2))
               y (range (dec (second (:min bounds))) (+ (second (:max bounds)) 2))
               z (range (dec (nth (:min bounds) 2)) (+ (nth (:max bounds) 2) 2))
               w (range (dec (nth (:min bounds) 3)) (+ (nth (:max bounds) 3) 2))]
           (let [cell       [x y z w]
                 neighbors (count-neighbors-2 cubes cell)]
             (if (cubes cell)
               (when (#{2 3} neighbors) cell)
               (when (= 3 neighbors) cell))))
         (filter identity)
         set)))

(defn part-2
  "Solve part 2 of the problem, returning the sixth generation from the
  starting state using the part 2 rules. Optionally allows the initial
  state diagram to be passed as an argument for unit testing."
  ([]
   (part-2 input))
  ([rows]
   (count (nth (iterate step-2 (initial-state-2 rows)) 6))))
