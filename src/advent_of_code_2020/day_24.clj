(ns advent-of-code-2020.day-24
  "Solutions for day 24."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "Paths to the tiles to be flipped (puzzle input)."
  (-> (io/reader (io/resource "day_24.txt"))
      line-seq))

(def directions
  "The deltas to be applied to the coordinates of a tile in order to
  reach the tile in the specified direction. I am using cube
  coordinates to identify hex tiles as described in
  https://math.stackexchange.com/questions/2254655/hexagon-grid-coordinate-system"
  {:e [1 -1 0]
   :se [0 -1 1]
   :sw [-1 0 1]
   :w [-1 1 0]
   :nw [0 1 -1]
   :ne [1 0 -1]})

(defn move
  "Given a tile and a move direction, return the tile reached in that
  direction."
  [tile direction]
  (mapv + tile (directions direction)))

(defn next-direction
  "Simple brute-force parser to pick off the next direction indicated
  from the start of a tile path string, returning a tuple of the
  direction found and the remainder of the string to be considered
  later."
  [s]
  (cond
    (str/starts-with? s "e")
    [:e (subs s 1)]

    (str/starts-with? s "w")
    [:w (subs s 1)]

    (str/starts-with? s "ne")
    [:ne (subs s 2)]

    (str/starts-with? s "nw")
    [:nw (subs s 2)]

    (str/starts-with? s "se")
    [:se (subs s 2)]

    (str/starts-with? s "sw")
    [:sw (subs s 2)]

    :else
    (throw (Exception. (str "Unrecognized next direction: " s)))))

(defn follow-line
  "Given a tile path string, starts with the origin tile and repeatedly
  moves in the next direction indicated by the path string until the
  path is exhausted. Returns the tile we reached."
  [s]
  (loop [tile [0 0 0]
         path s]
    (if (empty? path)
      tile
      (let [[direction path] (next-direction path)]
        (recur (move tile direction) path)))))

(defn lay-floor
  "Given a list of tile paths, identify which tile each represents, and
  flip it over. Returns a map whose keys are tile coordinates and
  whose values are truthy if that tile is black-side-up."
  [lines]
  (reduce (fn [acc line]
            (let [tile (follow-line line)]
              (update acc tile not)))
          {}
          lines))

(defn part-1
  "Solve part 1 of the problem. Flip over all the tiles identified by
  the paths in the problem input, and count how many are now black.
  Optionally allows the paths to be passed as an argument for unit
  testing with the sample problem data."
  ([]
   (part-1 input))
  ([lines]
   (let [floor (lay-floor lines)]
     (count (filter identity (vals floor))))))

(defn count-neighbors
  "Count the active cells adjacent to the specified cell. For part 2 of
  the problem we boil down the tile state map from part 1 into a
  simple set of the tile coordinates that are currently black, which
  is `tiles` here."
  [tiles tile]
  (apply +
         (for [direction (keys directions)]
           (if (tiles (move tile direction))
             1
             0))))

(defn neighborhood
  "Given a set of black tiles, expands the set to include any tiles
  whose state might change during a generation of the floor's cellular
  automata rules. This means we add any tile that is a direct neighbor
  of the current black set."
  [tiles]
  (reduce (fn [acc tile]
            (clojure.set/union acc #{tile}
                               (set (for [direction (keys directions)]
                                      (move tile direction)))))
          #{}
          tiles))

(defn step
  "Apply the rules for computing the next generation from the current
  state, and return the resulting set of active cells. As the previous
  function suggests, it was time to implement the optimization that
  had been identified but deferred on day 17, because the patterns
  grew spread out in space, and evaluating the entire bounding cube on
  each generation grew as the cube of the floor size, which started to
  take too long after ninety generations or so. Instead, we consider
  only tiles which are close enough to existing black tiles that their
  state might change."
  [tiles]
  (->> (for [tile (neighborhood tiles)]
         (let [neighbors (count-neighbors tiles tile)]
           (if (tiles tile)
             (when (#{1 2} neighbors) tile)
             (when (= 2 neighbors) tile))))
       (filter identity)
       set))

(defn part-2
  "Solve part 2 of the problem. Using the output of part 1 as the
  starting state, convert that into a simple set of active cell
  coordinates, then run a hundred iterations of the generation rules,
  and count the tiles which are black at the end of that. Optionally
  allows the paths to be passed as an argument for unit testing with
  the sample problem data."
  ([]
   (part-2 input))
  ([lines]
   (let [floor (->> (lay-floor lines)
                    (reduce (fn [acc [tile state]]
                              (if state
                                (conj acc tile)
                                acc))
                            #{}))]
     (count (nth (iterate step floor) 100)))))
