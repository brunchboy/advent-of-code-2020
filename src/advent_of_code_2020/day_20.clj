(ns advent-of-code-2020.day-20
  "Solutions for day 20."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The raw tile data, split at blank lines."
  (str/split (slurp (io/resource "day_20.txt")) #"\n\n"))

(defn read-tiles
  "Builds a map of tile IDs to vectors of strings holding the content of
  that tile."
  ([]
   (read-tiles input))
  ([raw-tiles]
   (reduce (fn [acc split-tile]
             (let [[header & rows] split-tile
                   [_ id] (re-matches #"Tile (\d+):" header)]
               (assoc acc (Long/parseLong id) (vec rows))))
           {}
           (map str/split-lines raw-tiles))))

(defn edges
  "Returns all four edges of a tile, in the order top, bottom, left,
  right."
  [tile]
  [(nth tile 0)
   (peek tile)
   (apply str (map #(subs % 0 1) tile))
   (apply str (map #(subs % (dec (count %))) tile))])

(defn transformed-edges
  "Build a set of every orientation of each edge of the tile supplied
  tile."
  [tile]
  (let [e (edges tile)]
    (set (concat e (map str/reverse e)))))

(defn outside-edge?
  "Given a tile id, one of its edges, and the sets of transformed edges
  for all tiles, see if this is an outside edge, which will mean it
  does not have matches in the sets of transformed edges for all tiles
  but this one."
  [id candidate-edges edge]
  (not-any? (fn [[candidate-id edge-set]]
              (when (not= id candidate-id)
                (edge-set edge)))
            candidate-edges))

(defn count-outside-edges
  "Give a tile, and the sets of transformed edges for all tiles, see how
  many edges of this tile must be an outside edge."
  [[id tile] candidate-edges]
  (->> (edges tile)
       (filter (partial outside-edge? id candidate-edges))
       count))

(defn find-candidate-edges
  "Builds a map whose IDs are tile IDs, and whose values are the sets
  of all possible edge orientations for that tile, for efficient
  match-testing."
  [tiles]
  (reduce-kv (fn [acc id tile]
               (assoc acc id (transformed-edges tile)))
             {}
             tiles))

(defn find-corners
  "Returns only the tiles which have at least two edges that do not
  match up with any other tile. The arity with candidate-edges is for
  part 2, where we have it precomputed."
  ([tiles]
   (find-corners (find-candidate-edges tiles)))
  ([tiles candidate-edges]
   (filter #(< 1 (count-outside-edges % candidate-edges)) tiles)))

(defn part-1
  "Find the corner pieces and multiply their IDs. Note that we don't
  have to get anywhere close to actually assembling the image to
  achieve this!"
  []
  (->> input
       read-tiles
       find-corners
       (map first)
       (apply *)))

(defn count-matches-for-edge
  [id candidate-edges edge]
  (count (filter (fn [[candidate-id edge-set]]
                   (when (not= id candidate-id)
                     (edge-set edge)))
                 candidate-edges)))

(defn count-edge-matches-for-tile
  [candidate-edges [id tile]]
  (for [edge (edges tile)]
    (count-matches-for-edge id candidate-edges edge)))

(defn crop
  "Removes the outer edges of a tile which are used only for
  registartion."
  [tile]
  (map #(subs % 1 (dec (count %))) (butlast (rest tile))))

(defn rotate-tile
  [tile]
  (mapv #(apply str %) (apply map (comp reverse str) tile)))

(defn flip-tile
  [tile]
  (mapv clojure.string/reverse tile))

(defn orient-tile
  "Generates a possible iteration of the tile, 0 means original
  orientation, 1 through 3 are un-flipped rotations, 4 is flipped, 5
  through 7 are flipped rotations."
  [tile n]
  (let [rotated (nth (iterate rotate-tile tile) (mod n 4))]
    (if (< n 4)
      rotated
      (flip-tile rotated))))

(defn find-match-for-edge
  "Returns ID of the tile which has an edge matching the supplied one."
  [id candidate-edges edge]
  (let [matches (->> (filter (fn [[candidate-id edge-set]]
                               (when (not= id candidate-id)
                                 (edge-set edge)))
                             candidate-edges))]
    (when (not= 1 (count matches))
      (throw (Exception. (str "Found " (count matches) " matches for edge!"))))
    (first (first matches))))

(defn match-orientation
  [edge i tile]
  (let [matches (->> (map (partial orient-tile tile) (range 8))
                     (filter #(= (nth (edges %) i) edge)))]
    (when (not= 1 (count matches))
      (throw (Exception. (str "Found " (count matches) " matching orientations."))))
    (first matches)))

(defn arrange
  [tiles candidate-edges]
  ;; Here is where I would add code to rotate the first corner until
  ;; its top and left edges were outside edges, but it turns out my
  ;; puzzle input already had it that way.
  (let [tile (first (find-corners tiles candidate-edges))]
    (loop [arranged [tile]]
      (if (= (count arranged) 144)
        arranged
        (let [x (mod (count arranged) 12)
              y (quot (count arranged) 12)]
          (if (zero? y)
            (let [[id tile] (peek arranged)
                  edge      (peek (edges tile))
                  next-id   (find-match-for-edge id candidate-edges edge)
                  next-tile (tiles next-id)]
              (recur (conj arranged [next-id (match-orientation edge 2 next-tile)])))
            (let [[id tile] (nth arranged (+ x (* 12 (dec y))))
                  edge      (nth (edges tile) 1)
                  next-id   (find-match-for-edge id candidate-edges edge)
                  next-tile (tiles next-id)]
              (recur (conj arranged [next-id (match-orientation edge 0 next-tile)])))))))))

(def monster-coordinates
  [[18 0]
   [0 1] [5 1] [6 1] [11 1] [12 1] [17 1] [18 1] [19 1]
   [1 2] [4 2] [7 2] [10 2] [13 2] [16 2]])

(defn monster-at?
  [image x y]
  (every? (fn [[monster-x monster-y]] (= (get-in image [(+ y monster-y) (+ x monster-x)]) \#))
          monster-coordinates))

(defn part-2
  []
  (let [tiles           (read-tiles input)
        candidate-edges (find-candidate-edges tiles)
        arranged        (->> (arrange tiles candidate-edges)
                             (map (comp crop second)))
        chunks          (map #(apply map str %) (partition 12 arranged))
        image           (vec (apply concat chunks))
        monsters        (apply + (for [i (range 8)]
                                   (let [oriented (orient-tile image i)]
                                     (count (filter identity (for [x (range (- (count (first oriented)) 20))
                                                                   y (range (- (count oriented) 2))]
                                                               (monster-at? oriented x y)))))))
        hashes          (count (filter #(= \# %) (apply str image)))]
    (- hashes (* monsters (count monster-coordinates)))))
