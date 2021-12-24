(ns advent-of-code-2021.day-23
  "Solutions for day 23."
  (:require [clojure.string :as str]))

(def costs
  "The costs of moving each of the types of amphipods"
  {"A" 1
   "B" 10
   "C" 100
   "D" 1000})

(defn populate-rooms
  "Helper function to set up original room state for a problem
  configuration. Takes a list of strings representing the contents of
  each room, listing amphipods in that room from top to bottom."
  [& rooms]
  (let [hall {:type    :hall
              :content ""}]
    {:energy 0
     :depth (count (first rooms))
     :world  (vec (concat
                   [hall]
                   (interleave (repeat hall)
                               (map-indexed (fn [i content]
                                              {:type    :room
                                               :content content
                                               :target  (str (char (+ i 65)))})
                                            rooms))
                   [hall hall]))}))

(defn hall?
  "Checks whether a space on in the world is a hallway."
  [space]
  (= (:type space) :hall))

(defn passable?
  "Checks whether the space is either an empty hallway, or a room
  entrance (which will always be empty), so we can move past it.)"
  [space]
  (or (not (hall? space))
      (str/blank? (:content space))))

(defn needs-emptying?
  "Checks whether a section of the world needs to be emptied to progress
  towards the solution. If it is a hallway (which we already know has
  an amphipod in it) this is always true. If it is a room, it is true
  if there are any amphipods in it which do not belogn in that room."
  [space]
  (or (hall? space)
      (some #(not= (:target space) %) (map str (:content space)))))

(defn can-move-into-room?
  "Checks whether an amphpod can move into a room, which means it must
  be the target room for that type of amphipod (which automatically
  filters out spaces that aren't rooms at all), and must be occupied
  only by other amphipods of the that type."
  [mover room]
  #_(println "cmir?" mover room)
  (and (= mover (:target room))
       (every? #(= mover %) (map str (:content room)))))

(defn can-move-into-hall?
  "Checks whether an amphpod can move into a hallway spot, which means
  it must be an actual hallway, and empty."
  [room]
  #_(println "cmih?" mover room)
  (and (hall? room)
       (str/blank? (:content room))))

(defn add-to-space
  "Returns the result of adding the specified amphipod to the space with
  the specified index."
  [world mover index]
  (update-in world [index :content] #(str mover %)))

(defn remove-from-space
  "Returns the amphipod removed from the space with the specified index,
  and the world after that removal has taken palce."
  [world index]
  (let [content (get-in world [index :content])]
    [(subs content 0 1) (update-in world [index :content] subs 1)]))

(defn moves-to-enter
  "Returns the number of moves requried for an amphipod to enter the
  specified room from the hallway above it, to reach the bottom
  unoccupied space, given the state of the problem, which includes the
  depth of the rooms."
  [room {:keys [depth]}]
  (- depth (count (:content room))))

(defn legal-moves-from-hall
  "Returns the states of the world resulting from all legal moves from
  the hallway spot at the specified index in the linear world, along
  with the cumulative energy cost after that move. The only legal move
  from a hallway spot is into the amphipod's destination room."
  [{:keys [world energy] :as state} index]
  (let [[mover world] (remove-from-space world index)
        cost          (costs mover)]
    (loop [i     (dec index) ; Start by looking to the left.
           moves []]
      (if-let [space (get world i)]
        (if (can-move-into-room? mover space)
          (recur (dec i)
                 (conj moves (assoc state
                                    :world (add-to-space world mover i)
                                    :energy (+ energy (* cost (+ (Math/abs (- index i))
                                                                 (moves-to-enter space state)))))))
          (recur (if (passable? space) (dec i) -1) moves))
        (loop [i     (inc index) ; Continue by looking to the right.
               moves moves]
          (if-let [space (get world i)]
            (if (can-move-into-room? mover space)
              (recur (inc i)
                     (conj moves (assoc state
                                        :world (add-to-space world mover i)
                                        :energy (+ energy (* cost (+ (Math/abs (- index i))
                                                                     (moves-to-enter space state)))))))
              (recur (if (passable? space) (inc i) -1) moves))
            moves))))))

(defn legal-moves-from-room
  "Returns thte states of the world resulting from all legal moves from
  the room at the specified index in the linear world, along with the
  cumulative energy cost after that move. Only considers moves into a
  hallway spot, but there must be one on the way to a later legal move
  into a room."
  [{:keys [world energy] :as state} index]
  #_(println "lmfr" index)
  (let [[mover world] (remove-from-space world index)
        cost          (costs mover)
        exit-moves    (moves-to-enter (get world index) state)]
    (loop [i     (dec index) ; Start by looking to the left.
           moves []]
      (if-let [space (get world i)]
        (if (can-move-into-hall? space)
          (recur (dec i)
                 (conj moves (assoc state
                                    :world (add-to-space world mover i)
                                    :energy (+ energy (* cost (+ (Math/abs (- index i)) exit-moves))))))
          (recur (if (passable? space) (dec i) -1) moves))
        (loop [i     (inc index) ; Continue by looking to the right.
               moves moves]
          (if-let [space (get world i)]
            (if (can-move-into-hall? space)
              (recur (inc i)
                     (conj moves (assoc state
                                        :world (add-to-space world mover i)
                                        :energy (+ energy (* cost (+ (Math/abs (- index i)) exit-moves))))))
              (recur (if (passable? space) (inc i) -1) moves))
            moves))))))

(defn legal-moves-from
  "Enumerates all the states of the world resulting from legal moves
  from the linear map space with the specified index, along with the
  cumulative energy cost after that move."
  [{:keys [world] :as state} index]
  (let [start (get world index)]
    (when (and (not (str/blank? (:content start)))
               (needs-emptying? start))
      (if (hall? start)
        (legal-moves-from-hall state index)
        (legal-moves-from-room state index)))))

(defn legal-moves
  "Enumerates all the states of the world resulting from legal moves
  from the supplied world state, along with the cumulative energy cost
  after that move."
  [{:keys [world] :as state}]
  (filter identity (mapcat (partial legal-moves-from state) (range (count world)))))

(defn solved?
  "Checks whether a state represents a valid solution (all amphipods in
  their destination rooms)."
  [{:keys [world]}]
  (when (every? (fn [space]
                  (if (hall? space)
                    (can-move-into-hall? space)
                    (let [mover (:target space)]
                      (can-move-into-room? mover space))))
                world)
    true))

(defn solve
  "Returns the energy cost required to solve the problem from the
  specified state."
  ([state]
   (solve state Double/POSITIVE_INFINITY))
  ([{:keys [energy] :as state} best-so-far]
   (cond (>= energy best-so-far)
         best-so-far

         (solved? state)
         energy

         :else
         (loop [best-so-far best-so-far
                moves       (legal-moves state)]
           (if (empty? moves)
             best-so-far
             (recur (solve (first moves) best-so-far)
                    (rest moves)))))))

(def part-1-input
  "The puzzle input for part 1."
  (populate-rooms "AD" "CD" "BB" "AC"))

(def part-2-input
  "The puzzle input for part 2."
  (populate-rooms "ADDD" "CCBD" "BBAB" "AACC"))
