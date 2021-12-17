(ns advent-of-code-2021.day-17
  "Solutions for day 17.")

(def input
  "The target bounds (puzzle input)."
  [[235 259] [-118 -62]])

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([[[_min-x _max-x] [min-y _max-y]]]
   (long (Math/floor (/ (* (double min-y) (inc min-y)) 2)))))

(defn hit?
  "Tests whether the specified point falls within the target."
  [[[min-x max-x] [min-y max-y]] [x y]]
  (and (<= min-x x max-x)  (<= min-y y max-y)))

(defn overshot?
  "Tests whether the specified point has gone past the target."
  [[[_min-x max-x] [min-y _max-y]] [x y]]
  (or (> x max-x) (< y min-y)))

(defn step
  "Advance one time unit within the physical simulation specified by the
  problem statement. Apply the current velocity to the projectile's
  position, and then the appropriate acceleration."
  [[[x y] [vx vy]]]
  [[(+ x vx) (+ y vy)] [(if (pos? vx) (dec vx) 0) (dec vy)]])

(defn hits?
  "Test whether a particular velocity will result in a hit to the
  target."
  [target v]
  (loop [x 0
         y 0
         v v]
    (cond
      (hit? target [x y])
      true

      (overshot? target [x y])
      false

      :else
      (let [[[x y] v] (step [[x y] v])]
        (recur x y v)))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([target]
   (->> (for [vx (range 1000)
              vy (range -1000 1000)]
          (hits? target [vx vy]))
        (filter identity)
        count)))
