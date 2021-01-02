(ns advent-of-code-2017.day-14
  (:require [clojure.set :as set]
            [advent-of-code-2017.day-10 :as day-10]))

(def input
  "The prefix string (puzzle input)."
  "amgozmfv")

(defn count-one-bits-in-hex-string
  "Given a hexadecimal value of arbitrary length, returns the number of
  `1`s present in the binary representation of the same value."
  [s]
  (->> (mapcat #(Integer/toBinaryString (Integer/parseInt (str %) 16)) s)
       (remove #(= \0 %))
       count))

(defn part-1
  "Solve part 1 of the puzzle: Given a prefix string, calculate the knot
  hash for the results of appending a hyphen and the values 0 through
  127 to that string, and count the number of 1 bits in all the
  results."
  []
  (reduce (fn [acc index]
            (+ acc (count-one-bits-in-hex-string (day-10/part-2 (str input "-" index)))))
          0
          (range 128)))

(defn hex-to-binary-string
  "Given a hexadecimal value of arbitrary length, returns the binary
  representation of the same value."
  [s]
  (->> (apply str (map (fn [c]
                         (let [bits (Integer/toBinaryString (Integer/parseInt (str c) 16))]
                           (str (subs "0000" 0 (- 4 (count bits))) bits)))
                       s))))

(defn build-bit-set
  "Creates a set whose elements are [x y] tuples where x is the index of
  every `1` bit in the supplied binary string `s` (starting with index
  zero at the left of the string) and `y` is the fixed value supplied
  as an argument."
  [s y]
  (reduce (fn [acc index]
            (if (= \1 (nth s index))
              (conj acc [index y])
              acc))
          #{}
          (range (count s))))

(defn build-used-grid
  "Creates a set of whose elements are the [x y] coordinates of each
  used square in the disk."
  []
  (reduce (fn [acc index]
            (set/union acc
                       (build-bit-set (hex-to-binary-string (day-10/part-2 (str input "-" index))) index)))
          #{}
          (range 128)))

(defn remove-region
  "Removes a contiguous region of bits (by horizontal or vertical
  connection) from the used grid, by simple recursive flood fill from
  the specified starting point. If that point is not used, then the
  grid is removed unchanged. Otherwise, that point is removed from the
  grid, and `remove-region` is called on each of its four neighbors in
  turn."
  [bit-grid [x y]]
  (if (bit-grid [x y])
    (-> (disj bit-grid [x y])
        (remove-region [(dec x) y])
        (remove-region [x (dec y)])
        (remove-region [(inc x) y])
        (remove-region [x (inc y)]))
    bit-grid))

(defn count-regions
  "Counts how many contiguous regions exist in the bit grid by removing
  each until none remain."
  [bit-grid]
  (loop [n        0
         bit-grid bit-grid]
    (if (empty? bit-grid)
      n
      (recur (inc n)
             (remove-region bit-grid (first bit-grid))))))

(defn part-2
  "Solve part 2 of the problem by realizing the bit grid represented by
  the hash values resulting from the problem input, then counting the
  regions of adjacent bits found within it."
  []
  (count-regions (build-used-grid)))
