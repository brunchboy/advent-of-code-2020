(ns advent-of-code-2023.day-16
  "Solution for day 16."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def sample-input
  "The sample data."
  ".|...\\....
|.-.\\.....
.....|-...
........|.
..........
.........\\
..../.\\\\..
.-.-/..|..
.|....-|.\\
..//.|....")

(defn move-beam
  "Advance a beam a single step in the direction it is moving, ignoring
  the boundaries of the grid."
  [[x y direction]]
  (case direction
    :right [(inc x) y direction]
    :left  [(dec x) y direction]
    :up    [x (dec y) direction]
    :down  [x (inc y) direction]))

(def mirror-bounces
  "The direction changes caused by the two different kinds of mirrors."
  {\\ {:right :down
       :left  :up
       :down  :right
       :up    :left}
   \/ {:right :up
       :left  :down
       :down  :left
       :up    :right}})

(defn beam-interaction
  "Given a beam and the content of a square it has reached, return the
  beams it will be in the next step, taking into account its
  direction, and any mirrors or splitters, but ignoring the boundaries
  of the grid."
  [[x y direction] cell]
  (case cell
    \.      [(move-beam [x y direction])]
    (\\ \/) [(move-beam [x y (get-in mirror-bounces [cell direction])])]
    \|      (if (#{:right :left} direction)
              [(move-beam [x y :up])
               (move-beam [x y :down])]
              [(move-beam [x y direction])])
    \-      (if (#{:up :down} direction)
               [(move-beam [x y :left])
                (move-beam [x y :right])]
               [(move-beam [x y direction])])))

(defn in-bounds
  "Returns truthy if the specified beam falls within the specified grid."
  [grid [x y]]
  (and (not (neg? x))
       (< x (count (first grid)))
       (not (neg? y))
       (< y (count grid))))

(defn step-beam
  "Given a description of a light beam arriving at a grid square,
  determine where it ends up next. Returns a set of zero, one, or two
  beam descriptions depending on if the beam falls off the grid,
  becomes a duplicate of a previously-seen beam, or is split."
  [grid previous-beams [x y direction]]
  (let [new-beams (filter (partial in-bounds grid) (beam-interaction [x y direction] (get-in grid [y x])))]
    (set/difference (set new-beams) previous-beams)))


(defn step
  "Advance one step in time in the puzzle state."
  [{:keys [beams previous-beams grid] :as state}]
  (let [new-beams (set (mapcat (partial step-beam grid previous-beams) beams))]
    (-> state
        (update :energized set/union (reduce (fn [acc [x y]] (conj acc [x y]))
                                             #{}
                                             new-beams))
        (assoc :beams new-beams)
        (update :previous-beams set/union new-beams))))

(def input
  "The actual puzzle input."
  (-> (io/resource "2023/day_16.txt")
      slurp))

(defn count-energized
  "Determines how many cells are energized given a starting beam."
  [data beam]
  (let [state {:beams #{beam}
                :energized #{(vec (take 2 beam))}
                :previous-beams #{}
                :grid (str/split-lines data)}]
     (loop [state (step state)]
       (if (empty? (:beams state))
         #_(doseq [y (range (count (:grid state)))]
           (doseq [x (range (count (first (:grid state))))]
             (print (if ((:energized state) [x y])  "#" ".")))
           (println))
         (count (:energized state))
         (recur (step state))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (count-energized data [0 0 :right])))

(defn starting-beams
  "Enumerate all the valid ways the beam can enter the grid."
  [data]
  (let [grid   (str/split-lines data)
        width  (count (first grid))
        height (count grid)]
    (concat
     (for [x (range width)]
       [x 0 :down])
     (for [x (range width)]
       [x (dec height) :up])
     (for [y (range height)]
       [0 y :right])
     (for [y (range height)]
       [(dec width) y :left]))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (reduce max (map (partial count-energized data) (starting-beams data)))))
