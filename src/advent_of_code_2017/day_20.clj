(ns advent-of-code-2017.day-20
  (:require [clojure.java.io :as io]))

(def input
  "The particle specifications (puzzle input)."
  (-> "2017/day_20.txt"
      io/resource
      io/reader
      line-seq))

(defn read-particle
  "Reads a particle specification line into a map that can be more
  conveniently worked with."
  [index line]
  (if-let [[_ px py pz vx vy vz ax ay az]
           (re-matches #"p=<(-?\d+),(-?\d+),(-?\d+)>, v=<(-?\d+),(-?\d+),(-?\d+)>, a=<(-?\d+),(-?\d+),(-?\d+)>" line)]
    {:p     (mapv #(Long/parseLong %) [px py pz])
     :v     (mapv #(Long/parseLong %) [vx vy vz])
     :a     (mapv #(Long/parseLong %) [ax ay az])
     :index index}
    (throw (Exception. (str "Unable to parse particle " line)))))

(defn magnitude
  "Calculate acceleration, velocity, or distance magnitude by summing
  their squares and taking the square root of the result (which value
  is being considered is controlled by the keyword `k`)."
  [k particle]
  (Math/sqrt (->> particle
                  k
                  (map #(* % %))
                  (apply +))))

(defn part-1
  "Solve part 1 by sorting by magnitudes of acceleration, velocity, and
  position, and returning the smallest. It turns out that velocity
  alone was unique in my case, but this is a safer general solution."
  [input]
  (let [particles (map-indexed read-particle input)]
    (-> (sort-by (fn [particle] [(magnitude :a particle) (magnitude :v particle) (magnitude :p particle)])
                 particles)
        first
        :index)))

(defn accelerate
  "Updates a particle's velocity given its acceleration."
  [particle]
  (update particle :v (partial mapv +) (:a particle)))

(defn move
  "Updates a particle's positiion given its velocity."
  [particle]
  (update particle :p (partial mapv +) (:v particle)))

(defn collide
  "Removes any particles that are now occupying the same position."
  [system]
  (let [position-frequencies (frequencies (map :p system))]
    (filter #(= 1 (position-frequencies (:p %)))  ; Get rid of any particles with a non-unique position.
            system)))

(defn step
  "Apply one step of the simulation to the particle system. Updates the
  velocities, then positions, then removes collided particles."
  [system]
  (->> system
       (map accelerate)
       (map move)
       collide))

(defn system-sequence
  "Returns a lazy sequence of the stages of a particle system with the
  specified initial states."
  [input]
  (iterate step (map-indexed read-particle input)))

(defn part-2
  "To solve this, I just called `system-sequence` and then mapped
  `count` over the result, and explored the resulting count sequences
  in CIDER. It stopped changing after 39 steps, although I scrolled
  down to 1,000 or so to be sure. Here is a programatic recreation of
  that experiment, which yielded an accepted answer."
  [input]
  (count (nth (system-sequence input) 1000)))
