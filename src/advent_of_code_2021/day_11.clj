(ns advent-of-code-2021.day-11
  "Solutions for day 11.")

(def input
  "The puzzle input."
  [4 1 1 2 2 5 6 3 7 2
   3 1 4 3 2 5 3 7 1 2
   4 5 1 6 8 4 8 6 3 1
   3 7 8 3 4 7 7 1 3 7
   3 7 4 6 7 2 3 5 8 2
   5 8 6 1 3 5 8 8 8 4
   4 8 4 3 3 5 1 7 7 4
   2 3 1 6 4 4 7 6 2 1
   6 6 4 3 8 1 7 7 4 5
   6 3 6 6 8 1 5 8 6 8])

(defn neighbors
  "Given the coordinates of a squid, an [x y] tuple, returns a list of
  the coordinate tuples of all direct neighbors of that squid,
  horizontal, vertical, or diagonal, which lie within the boundaries
  of the cave."
  [[x y]]
  (->> (for [dx (range 3)
             dy (range 3)]
         [(+ x dx -1)
          (+ y dy -1)])
       (filter #(not= [x y] %))
       (filter (fn [[x y]] (and (<= 0 x 9)
                                (<= 0 y 9))))))

(defn get-octopus
  "Given the cave grid as a simple vector, return the value at a
  given [x y] tuple within it."
  [grid [x y]]
  (nth grid (+ x (* y 10))))

(defn increment-exposed
  "Given the cave grid holding current squid energies in a simple
  vector, increment the energy of all squids whose coordinate tuples
  are in the `exposed` list."
  [grid exposed]
  (loop [grid    grid
         exposed exposed]
    (if (empty? exposed)
      grid
      (let [[x y] (first exposed)]
        (recur (update grid (+ x (* y 10)) inc)
               (rest exposed))))))

(defn zero-flashed
  "Given the cave grid holding current squid energies in a simple
  vector, set to zero the energy of all squids whose coordinate tuples
  are in the `flashed` list."
  [grid flashed]
  (loop [grid grid
         flashed (seq flashed)]
    (if (empty? flashed)
      grid
      (let [[x y] (first flashed)]
        (recur (assoc grid (+ x (* y 10)) 0)
               (rest flashed))))))

(defn step
  "Advance the time in our squid simulation by one interval,
  incrementing the energy of all squids by 1, then calculating which
  flash, including those triggered by the initial set of flashes,
  following the problem rules. `flashes` holds the cumulative flash
  count so far, and is returned updated to reflect the number of
  flashes which occurred during this step."
  [[grid flashes]]
  (loop [grid       (mapv inc grid)
         flashed    #{}
         candidates (for [x (range 10)
                          y (range 10)]
                      [x y])]
    (if (empty? candidates)
      [(zero-flashed grid flashed) (+ flashes (count flashed))]
      (let [current (first candidates)]
        (if (or (<= (get-octopus grid current) 9)
                (flashed current))
          (recur grid flashed (rest candidates))
          (let [exposed (neighbors current)]
            (recur (increment-exposed grid exposed)
                   (conj flashed current)
                   (concat candidates exposed))))))))

(defn part-1
  "Solve part 1"
  ([]
   (part-1 input))
  ([data]
   (let [[_grid flashes] (first (drop 100 (iterate step [data 0])))]
     flashes)))

(defn part-2
  "Solve part 2"
  ([]
   (part-2 input))
  ([data]
   (count (take-while (fn [[grid _]] (some pos? grid)) (iterate step [data 0])))))
