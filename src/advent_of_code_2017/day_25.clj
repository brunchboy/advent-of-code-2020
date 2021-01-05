(ns advent-of-code-2017.day-25
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The Turing machine blueprint (puzzle input)."
  (-> "2017/day_25.txt"
      io/resource
      io/reader
      line-seq))

(defn sections
  "Returns a sequence of each meaningful section of the blueprint, i.e.
  split at the blank lines."
  [coll]
  (->> coll
       (partition-by str/blank?)
       (take-nth 2)))

(defn read-initial-state
  "Determines the starting state specified in the preamble."
  [line]
  (if-let [[_ state] (re-matches #"Begin in state (\w+)." line)]
    state
    (throw (Exception. (str "Problem reading starting state from: " line)))))

(defn read-diagnostic
  "Determines the step at which a diagnostic checksum should be
  performed from the second preamble line."
  [line]
  (if-let [[_ steps] (re-matches #"Perform a diagnostic checksum after (\d+) steps." line)]
    (Long/parseLong steps)
    (throw (Exception. (str "Problem reading diagnostic checksum step from: " line)))))

(defn read-current-value
  "Reads the specification of a current tape value from a state rule in
  the blueprint."
  [line]
  (if-let [[_ val] (re-matches #"\s*If the current value is (\d+):" line)]
    (Long/parseLong val)
    (throw (Exception. (str "Problem reading current tape value rule: " line)))))

(defn read-value-to-write
  "Reads the specification of a value to be written tot ape from a state
  rule in the blueprint."
  [line]
  (if-let [[_ val] (re-matches #"\s*- Write the value (\d+)." line)]
    (Long/parseLong val)
    (throw (Exception. (str "Problem reading tape value to write: " line)))))

(defn read-direction-to-move
  "Reads the direction to move in a state rule in the blueprint."
  [line]
  (if-let [[_ direction] (re-matches #"\s*- Move one slot to the (\w+)." line)]
    (if (#{"left" "right"} direction)
      (keyword direction)
      (throw (Exception. (str "Unrecognized tape movement direction: " direction))))
    (throw (Exception. (str "Problem reading direction to move: " line)))))

(defn read-next-state
  "Read the next state to enter in a state rule in the blueprint."
  [line]
  (if-let [[_ state] (re-matches #"\s*- Continue with state (\w+)." line)]
    state
    (throw (Exception. (str "Problem reading next target state from: " line)))))

(defn read-state-rule
  "Reads a single rule about what to do when a state finds a particular
  value on the tape. Returns a tuple of the tape value and a tuple of
  the value to be written, the direction to move, and the state to
  enter next."
  [rule]
  [(read-current-value (first rule))
   [(read-value-to-write (second rule))
    (read-direction-to-move (nth rule 2))
    (read-next-state (nth rule 3))]])

(defn read-state
  "Reads a single state specification from the turing machine blueprint.
  Returns a tuple of the key of that state and a map whose keys are
  potential current tape values (0 or 1). Each of these values is a
  tuple of the value to be written, the direction to move (`:left` or
  `:right`) and the state to enter next."
  [spec]
  (if-let [[_ state] (re-matches #"In state (\w+):" (first spec))]
    [state (into {} (map read-state-rule (partition 4 (rest spec))))]
    (throw (Exception. (str "Unable to read state name from state specification: " (first spec))))))

(defn read-states
  "Builds up the state transition map by parsing the sequence of
  blueprint sections which describe them. The result is a map whose
  keys are state names. Each value is in turn a map whose keys are
  potential current tape values (0 or 1). Each of these values is a
  tuple of the value to be written, the direction to move (`:left` or
  `:right`) and the state to enter next."
  [states]
  (into {} (map read-state states)))

(defn read-blueprint
  "Builds the initial state of our turing machine including its state
  table by parsing the blueprint."
  [blueprint]
  (let [[preamble & states] (sections blueprint)]
    {:state    (read-initial-state (first preamble))
     :pos      0
     :tape     #{}
     :states   (read-states states)
     :step     0
     :diagnose (read-diagnostic (second preamble))}))

(defn step
  "Performs one step of the Turing machine. Given an input machine
  state, returns the next."
  [{:keys [state pos tape states] :as machine}]
  (let [current                      (if (tape pos) 1 0)
        [write direction next-state] (get-in states [state current])]
    (-> machine
        (update :step inc)
        (assoc :state next-state)
        (update :pos (if (= direction :right) inc dec))
        (update :tape (if (pos? write) conj disj) pos))))

(defn state-sequence
  "Returns a lazy sequence of the states of the Turing machine specified
  by `blueprint`."
  [blueprint]
  (iterate step (read-blueprint blueprint)))

(defn part-1
  "Calculate the diagnostic checksum after running the turing machine
  defined by the blueprint for the number of steps specified."
  [blueprint]
  (let [states (state-sequence blueprint)
        steps  (:diagnose (first states))]
    (count (:tape (nth states steps)))))
