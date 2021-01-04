(ns advent-of-code-2017.day-21
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(def starting-image
  "The image that the enhancement process always begins with."
  [".#."
   "..#"
   "###"])

(def input
  "The enhancement rules (puzzle input)."
  (-> "2017/day_21.txt"
      io/resource
      io/reader
      line-seq))

(defn read-rule
  "Parses an enhancement rule line from the rule book into a tuple of
  source tile and expanded tile."
  [line]
  (let [[_ source expanded] (re-matches #"([.#/]+)\s+=>\s+([.#/]+)" line)]
    (mapv #(str/split % #"/") [source expanded])))

(defn build-rulebook
  "Reads the enhancement rule book and builds a map from source tiles to
  expanded tiles."
  [input]
  (into {} (map read-rule input)))

(defn rotate-tile
  "Rotates the content of the tile ninety degrees clockwise."
  [tile]
  (mapv #(apply str %) (apply map (comp reverse str) tile)))

(defn flip-tile
  "Flips the content of the tile over the Y axis."
  [tile]
  (mapv clojure.string/reverse tile))

(defn extract-tile
  "Extracts a tile of side `tile-size` from the full image, at the
  specified tile coordinates."
  [image tile-size x y]
  (vec (for [y (range (* y tile-size) (* (inc y) tile-size))]
         (subs (get image y) (* x tile-size) (* (inc x) tile-size)))))

(defn slice-image
  [image]
  (let [tile-size (if (zero? (mod (count (first image)) 2)) 2 3)]
    (vec (for [y (range (/ (count image) tile-size))]
           (vec (for [x (range (/ (count (first image)) tile-size))]
                  (extract-tile image tile-size x y)))))))

(defn merge-row
  "Given a row of tiles from a sliced image, returns the rows of the
  rejoined image section represented by those tiles."
  [row]
  (vec (for [y (range (count (first row)))]
         (apply str (map #(get % y) row)))))

(defn merge-slices
  "Given an image sliced into tiles, restore it to a single image."
  [slices]
  (vec (mapcat merge-row slices)))

(defn orient-tile
  "Generates a possible iteration of the tile, 0 means original
  orientation, 1 through 3 are un-flipped rotations, 4 is flipped, 5
  through 7 are flipped rotations. This could be made a lot more
  efficient by doing the rotations only once, and basically stepping
  from orientation n-1 to n, but it worked well enough for the
  problem."
  [tile n]
  (let [rotated (nth (iterate rotate-tile tile) (mod n 4))]
    (if (< n 4)
      rotated
      (flip-tile rotated))))

(defn expand-tile
  "Given a tile, find the matching expansion rule for some flipped or
  rotated orientation, and return the expanded value."
  [rule-book tile]
  (->> (map (partial orient-tile tile) (range 8))
       (map rule-book)
       (filter identity)
       first))

(defn expand-row
  "Given a row of tiles, expand each one, and return the expanded row."
  [rule-book row]
  (mapv (partial expand-tile rule-book) row))

(defn enhance
  "Applies one step of the enhancement rules process to an image.
  Returns a tuple in the same format as its input, containing the rule
  book and the enhanced image."
  [rule-book image]
  (->> image
       slice-image
       (mapv (partial expand-row rule-book))
       merge-slices))

(defn build-enhancer
  "Returns a lazy sequence of successive enhancements of the supplied
  image using the supplied rule book."
  [rule-book image]
  (iterate (fn [img] (enhance rule-book img)) image))

(defn part-1
  "Solve part 1 of the problem by reading the rule book, running the
  specified number of iterations of the enhancement algorithm, and
  counting the active pixels in the result."
  [input iterations]
  (let [rule-book (build-rulebook input)
        result (nth (build-enhancer rule-book starting-image) iterations)]
    (->> result
         (apply str)
         (filter #(= \# %))
         count)))
