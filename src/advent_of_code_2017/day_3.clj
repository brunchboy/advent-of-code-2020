(ns advent-of-code-2017.day-3)

(defn square-size
  "Calculates the number of rows or columns required in a grid big
  enough to hold the specified number. We find the square root of the
  smallest odd square number larger than or equal to the target
  number."
  [n]
  (let [result (long (Math/ceil (Math/sqrt n)))]
    (if (even? result)
      (inc result)
      result)))

(defn coordinates
  "Finds the coordinates at which the specified number will be found in
  the spiral grid, where 1 is at the origin."
  [n]
  (let [edge (square-size n)
        max  (* edge edge)
        axes (/ (dec edge) 2)]  ; The max value of any axis.
    (cond (< n 2)  ; The trivial case, we are already at the origin.
          [0 0]

          (> n (- max edge))  ; We are along the bottom edge.
          [(- axes (- max n)) (- axes)]

          (> n (- max edge (dec edge)))  ; We are along the left edge.
          [(- axes) (- (- max edge n -1) axes)]

          (> n (- max edge (dec edge) (dec edge)))  ; We are along the top edge
          [(- max edge edge n axes -2) axes]

          :else ; We are along the right edge.
          [axes (- axes (- max edge edge edge n -3))])))

(defn part-1
  "Returns the Manhattan distance from the specified memory cell to the
  origin."
  [cell]
  (->> cell
       coordinates
       (map #(Math/abs %))
       (apply +)))

(defn add-cell
  "Calculates the value that belongs in the specified cell (indexed from
  1). Requires that all cells with smaller index numbers (and no other
  cells) must have already been calculated in `cells`, which is a map
  keyed by cell coordinates (not indices). Returns a tuple of the
  updated cell map with the new value included, and the index of the
  next cell to be calculated. Handles the special case of initializing
  the map with the first cell."
  [[cells cell]]
  (if (< cell 2)
    [{[0 0] 1} 2]
    (let [destination (coordinates cell)
          coordinates (filter #(not= destination %)
                              (for [dx [-1 0 1]
                                    dy [-1 0 1]]
                                [(+ (first destination) dx) (+ (second destination) dy)]))
          new-value   (->> coordinates
                           (map (fn [neighbor]
                                  (get cells neighbor 0)))
                           (apply +))]
      [(assoc cells destination new-value) (inc cell)])))

(def cell-sequence
  "Holds the infinite sequence of growing cells."
  (iterate add-cell [{[0 0] 1} 2]))

(defn part-2
  "Solves part 2. Returns the first number from the memory spiral larger
  than the specified value."
  [v]
  (->> (for [[cells next] cell-sequence]
         (cells (coordinates (dec next))))
       (drop-while #(<= % v))
       first))
