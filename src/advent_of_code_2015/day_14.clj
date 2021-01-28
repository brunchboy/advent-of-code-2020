(ns advent-of-code-2015.day-14
  (:require [clojure.string :as str]))

(defn read-reindeer
  "Read a reindeer description line and build a tuple of its specs."
  [line]
  (let [[_ name speed flying resting] (re-matches #"(\w+) can fly (\d+) km/s for (\d+) seconds, but then must rest for (\d+) seconds."
                                                  line)]
    [(Long/parseLong speed) (Long/parseLong flying) (Long/parseLong resting) name]))

(def reindeer
  "The reindeer in the race (puzzle input). In retrospect, it's annoying
  that reindeer is the same word in singular and plural form, it makes
  several of the variables and arguments below ambiguous."
  (->> "Dancer can fly 27 km/s for 5 seconds, but then must rest for 132 seconds.
Cupid can fly 22 km/s for 2 seconds, but then must rest for 41 seconds.
Rudolph can fly 11 km/s for 5 seconds, but then must rest for 48 seconds.
Donner can fly 28 km/s for 5 seconds, but then must rest for 134 seconds.
Dasher can fly 4 km/s for 16 seconds, but then must rest for 55 seconds.
Blitzen can fly 14 km/s for 3 seconds, but then must rest for 38 seconds.
Prancer can fly 3 km/s for 21 seconds, but then must rest for 40 seconds.
Comet can fly 18 km/s for 6 seconds, but then must rest for 103 seconds.
Vixen can fly 18 km/s for 5 seconds, but then must rest for 84 seconds."
       str/split-lines
       (map read-reindeer)))

(defn distance
  "Calculate how far a reindeer can travel in the specified time. The
  reindeer is described by a tuple of speed (in km/s), flying
  time (seconds), and resting time (seconds)."
  [time [speed flying resting]]
  (let [cycle-time     (+ flying resting)
        full-cycles    (quot time cycle-time)
        partial-flying (min (mod time cycle-time) flying)
        total-flying   (+ (* full-cycles flying) partial-flying)]
    (* speed total-flying)))

(defn part-1
  "See how far the fastest reindeer can fly in the specified time."
  [time]
  (apply max (map (partial distance time) reindeer)))

(def expanded-reindeer
  "Move to a map structure so we can track more data easily setting up
  for the iterated-each-second solution to part 2."
  (map (fn [[speed flying resting name]]
         {:speed      speed
          :flying     flying
          :resting    resting
          :name       name
          :cycle-time (+ flying resting)
          :position   0
          :score      0})
       reindeer))

(defn move
  "Checks whether a reindeer is flying at the specified second, and if
  so updates their position based on their speed."
  [time {:keys [cycle-time flying speed] :as reindeer}]
  (cond-> reindeer
    (< (mod time cycle-time) flying)
    (update :position + speed)))

(defn score
  "Awards a point to any reindeer who are currently in the lead."
  [reindeer]
  (let [lead (apply max (map :position reindeer))]
    (map (fn [{:keys [position] :as racer}]
           (cond-> racer
             (= position lead)
             (update :score inc)))
         reindeer)))

(defn step
  "Process the passage of a second in the race, updating the current
  time and reindeer positions and scores accordingly."
  [[time reindeer]]
  [(inc time)
   (->> reindeer
        (map (partial move time))
        score)])

(defn part-2
  "Solve part 2."
  [time]
  (let [standings (second (nth (iterate step [0 expanded-reindeer]) time))]
    (apply max (map :score standings))))
