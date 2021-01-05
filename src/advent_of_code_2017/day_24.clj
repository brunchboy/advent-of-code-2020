(ns advent-of-code-2017.day-24
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as str]))

(def input
  "The list of components available (puzzle input)."
  (-> "2017/day_24.txt"
      io/resource
      io/reader
      line-seq))

(defn read-component
  "Reads a line of the component list, returning the tuple of port sizes
  it represents."
  [line]
  (mapv #(Long/parseLong %) (str/split line #"/")))

(defn can-connect?
  "Given a component, and a required port value checks whether the
  component can connect to that port (it has at least one port with
  that value)."
  [[a b] port]
  (or (= port a) (= port b)))

(defn other-port
  "Given a component and a port value returns the value of the port
  which is on the other side of the component."
  [[a b] port]
  (if (= port a)
    b
    a))

(defn valid-bridge?
  "Tests whether a list of ports constitutes a valid bridge, i.e. it
  starts with a starting component, and each component can connect to
  the next."
  [bridge]
  (loop [built           []
         left            bridge
         connection      0]
    (if (empty? left)
      (seq built)
      (let [current (first left)]
        (when (can-connect? current connection)
          (recur (conj built current)
                 (rest left)
                 (other-port current connection)))))))

(defn all-bridges
  "Returns all valid bridges that can be constructed from the specified
  list of components."
  [input]
  (->> (map read-component input)
       combo/subsets
       (mapcat combo/permutations)
       (filter valid-bridge?)))

(defn bridge-strength
  "Calculates the strength of a bridge, the sum of all its port values."
  [bridge]
  (apply + (flatten bridge)))

(defn slow-part-1
  "Brute force approach to part 1. Although this worked on the sample
  problem, as expected, it is far too slow to solve the real input
  data."
  [input]
  (->> input
       all-bridges
       (map bridge-strength)
       (apply max)))

(defn next-options
  "Given a set of components and the current port connection value
  required, returns a set of tuples for every possible component that
  can be used at this point. The tuple consists of the usable
  component, followed by the list of components which were not used."
  [components port]
  (loop [past    '()
         current (first components)
         future  (rest components)
         result  '()]
    (if (nil? current)
      result
      (recur (conj past current)
             (first future)
             (rest future)
             (if (can-connect? current port)
               (conj result [current (concat past future)])
               result)))))

(defn longest-bridges
  "A more heuristic approach, to find the longest bridges that can be
  built with the given set of components, bridge built so far, and
  required next connection port value. We only care about the longest
  bridges we can build because a longer bridge will always have a
  higher strength value than a shorter bridge."
  [bridge connection components]
  (let [extension-choices (next-options components connection)]
    (if (seq extension-choices)  ; There are components we can still add, see where each leads.
      (mapcat (fn [[component others]]
                (longest-bridges (conj bridge component) (other-port component connection) others))
              extension-choices)
      [bridge])))  ; We can build no further.

(defn part-1
  "Heuristic approach to part 1. Not quite a weighted search yet, but
  hopefully sufficiently more efficient to be usable."
  [input]
  (->> input
       (map read-component)
       (longest-bridges [] 0)
       (map bridge-strength)
       (apply max)))

(defn bridge-length-and-strength
  "Returns a tuple of a bridge's length in components and its
  strength (as calculated above)."
  [bridge]
  [(count bridge) (bridge-strength bridge)])

(defn part-2
  "Solve part 2: Sort the tuples of bridge length and strength, and take
  the first, which will break ties on length by strength. Return its
  strength."
  [input]
  (->> input
       (map read-component)
       (longest-bridges [] 0)
       (map bridge-length-and-strength)
       (sort (comp - compare))
       first
       second))
