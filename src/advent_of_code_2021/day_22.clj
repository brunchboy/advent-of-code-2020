(ns advent-of-code-2021.day-22
  "Solutions for day 22."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn parse-line
  "Given a string describing a boot operation, build a tuple of the
  action string (on or off) and six numbers bounding the rectangular
  prism, i.e. [action x-min x-max y-min y-max z-min z-max]."
  [line]
  (let [[action & bounds] (rest (re-matches #"(\w+) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)"
                                            line))]
    (vec (concat [action] (map #(Long/parseLong %) bounds)))))

(defn read-input
  "Read puzzle input from the specified resource path."
  [path]
  (->> (io/resource path)
       slurp
       str/split-lines
       (map parse-line)))

(def input
  "The puzzle input."
  (read-input "2021/day_22.txt"))

(defn sanitize-input-line
  "If this line is entirely outside the initialization procedure area,
  discard it. Otherwise clip it to fit within the area."
  [[action x-min x-max y-min y-max z-min z-max]]
  (when (and (<= x-min 50) (<= y-min 50) (<= z-min 50)
             (>= x-max -50) (>= y-max -50) (>= z-max -50))
    [action (max x-min -50) (min x-max 50) (max y-min -50) (min y-max 50) (max z-min -50) (min z-max 50)]))

(defn sanitize-input
  "Get rid of lines that are entirely outside the initialization procedure area,
  and clip others to fit within it."
  [steps]
  (->> steps
      (map sanitize-input-line)
      (filter identity)))

(defn boot-step
  "Run one step of the boot sequence, adding or removing cubes as
  appropriate."
  [cubes [action x-min x-max y-min y-max z-min z-max]]
  (reduce (fn [acc cube]
            (case action
              "on"  (conj acc cube)
              "off" (disj acc cube)))
          cubes
          (for [x (range x-min (inc x-max))
                y (range y-min (inc y-max))
                z (range z-min (inc z-max))]
            [x y z])))

(defn boot
  "Run the boot sequence, returning the set of cubes which are on at the
  end of it."
  [steps]
  (reduce boot-step
          #{}
          steps))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([steps]
   (count (boot (sanitize-input steps)))))

(defn intersect?
  "Checks whether two prisms have any overlapping cubes."
  [[x-min-1 x-max-1 y-min-1 y-max-1 z-min-1 z-max-1] [x-min-2 x-max-2 y-min-2 y-max-2 z-min-2 z-max-2]]
  (and (< x-min-2 x-max-1) (> x-max-2 x-min-1)
       (< y-min-2 y-max-1) (> y-max-2 y-min-1)
       (< z-min-2 z-max-1) (> z-max-2 z-min-1)))

(defn remove-intersection
  "If the target prism intersects the cutting prism, split it into the
  pieces which extend beyond the cutting prism, if any."
  [cutting-prism target-prism]
  (if (intersect? target-prism cutting-prism)
    (let [[x-min-cut x-max-cut y-min-cut y-max-cut z-min-cut z-max-cut] cutting-prism
          new-prisms                                                    (volatile! [])
          remaining-prism                                               (volatile! target-prism)]
      (when (< (get @remaining-prism 0) x-min-cut)  ; Target extends left of cut, split off that chunk.
        (vswap! new-prisms conj (assoc @remaining-prism 1 x-min-cut))
        (vswap! remaining-prism assoc 0 x-min-cut))
      (when (> (get @remaining-prism 1) x-max-cut)  ; Target extends right of cut, split off that chunk.
        (vswap! new-prisms conj (assoc @remaining-prism 0 x-max-cut))
        (vswap! remaining-prism assoc 1 x-max-cut))
      (when (< (get @remaining-prism 2) y-min-cut)  ; Target extends below cut, split off that chunk.
        (vswap! new-prisms conj (assoc @remaining-prism 3 y-min-cut))
        (vswap! remaining-prism assoc 2 y-min-cut))
      (when (> (get @remaining-prism 3) y-max-cut)  ; Target extends above cut, split off that chunk.
        (vswap! new-prisms conj (assoc @remaining-prism 2 y-max-cut))
        (vswap! remaining-prism assoc 3 y-max-cut))
      (when (< (get @remaining-prism 4) z-min-cut)  ; Target extends behind cut, split off that chunk.
        (vswap! new-prisms conj (assoc @remaining-prism 5 z-min-cut))
        (vswap! remaining-prism assoc 4 z-min-cut))
      (when (> (get @remaining-prism 5) z-max-cut)  ; Target extends in front of cut, split off that chunk.
        (vswap! new-prisms conj (assoc @remaining-prism 4 z-max-cut))
        (vswap! remaining-prism assoc 5 z-max-cut))
      @new-prisms)
    [target-prism]))

(defn boot-step-2
  "Run one step of the boot sequence, using constructive solid
  geometry."
  [prisms [action x-min x-max y-min y-max z-min z-max]]
  ;; The bounds of our prism are from the minimum coordinate of each
  ;; contained cube, to one greater than the maximum coordinate of
  ;; each contained cube, to make the volume computation and
  ;; intersection/splitting logic work. I forgot to add that increment
  ;; initially, and could not figure out for a good while why my
  ;; answers were coming in too low.
  (let [cutting-prism [x-min (inc x-max) y-min (inc y-max) z-min (inc z-max)]
        cut-prisms    (mapcat (partial remove-intersection cutting-prism) prisms)]
    (case action
      "on"  (conj cut-prisms cutting-prism)
      "off" cut-prisms)))

(defn boot-2
  "Run the boot sequence using constructive geometry, returning the
  resulting list of prisms."
  [steps]
  (reduce boot-step-2
          []
          steps))

(defn count-cubes
  "Counts the number of unit cubes contained in the specified list of
  rectangular prisms."
  [prisms]
  (reduce (fn [acc [x-min x-max y-min y-max z-min z-max]]
            (+ acc (* (- x-max x-min) (- y-max y-min) (- z-max z-min))))
          0
          prisms))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([steps]
   (count-cubes (boot-2 steps))))
