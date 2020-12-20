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

(defn find-corners
  "Returns only the tiles which have at least two edges that do not
  match up with any other tile."
  [tiles]
  (let [
        candidate-edges (reduce-kv (fn [acc id tile]
                                     (assoc acc id (transformed-edges tile)))
                              {}
                              tiles)]
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
