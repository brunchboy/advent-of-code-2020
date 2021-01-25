(ns advent-of-code-2015.day-7
  (:require [clojure.java.io :as io]
            [instaparse.core :as insta]))

(def circuit
  "The parser for circuit specification rules."
  (insta/parser
   "S = signal | direct | and | or | not | lshift | rshift
wire = #'[a-z]+'
number = #'\\d+'
<signal> = number | wire
direct = signal ' -> ' wire
and = signal ' AND ' signal ' -> ' wire
or = signal ' OR ' signal ' -> ' wire
not = 'NOT ' signal ' -> ' wire
lshift = signal ' LSHIFT ' number ' -> ' wire
rshift = signal ' RSHIFT ' number ' -> ' wire"))

(def input
  "The list of wiring instructions (the puzzle input)."
  (->> (io/resource "2015/day_7.txt")
       io/reader
       line-seq
       (map circuit)
       (map second)))

(defn gather-inputs
  "Given the map of wire names to known signals and a wiring rule, scans
  the rule to find the signals arriving at any input wires and returns
  those as a vector as long as all are known. Returns `nil` if any
  inputs are still unknown."
  [known instruction]
  (loop [current (first instruction)
         left    (rest instruction)
         result  []]
    (cond (= " -> " current)  ; We've reached the end of the inputs.
          result  ; Return whatever result we have accumulated.

          (and (sequential? current) (= :number (first current)))  ; This is a constant numeric input, gather it.
          (recur (first left)
                 (rest left)
                 (conj result (Long/parseLong (second current))))

          (and (sequential? current) (= :wire (first current)))  ; This is a wire input, check if it is resolved.
          (if-let [signal (known (second current))]
            (recur (first left)  ; Yes, gather the known signal at the input wire.
                   (rest left)
                   (conj result signal))
            nil)  ; No, we found an unresolved input, return `nil`.

          :else
          (recur (first left)  ; This is not an input, keep scanning.
                 (rest left)
                 result))))

(defn resolve-instruction
  "Given the map of wire names to known signals and a wiring rule, if
  the signal arriving at the rule's target wire can be determined,
  updates the map to include this information. Otherwise returns the
  map unchanged."
  [known instruction]
  (let [target (second (last instruction))]
    (if (known target)
      known  ; We already know this target, so we don't need to do anything.
      (if-let [inputs (gather-inputs known instruction)]  ; Check if we have everything to resolve it now.
        (assoc known target  ; Yes, resolve the target using the instruction and inputs.
               (case (first instruction)
                 :direct (first inputs)
                 :and    (apply bit-and inputs)
                 :or     (apply bit-or inputs)
                 :not    (bit-and (apply bit-not inputs) 65535)
                 :rshift (apply bit-shift-right inputs)
                 :lshift (bit-and (apply bit-shift-left inputs) 65535)))
        known))))  ; No, we were still missing at least one signal, we can't resolve the target yet.

(defn part-1
  "Apply all the wiring instructions using the currently-known signals,
  adding any signals that can now be resolved, until the results stop
  changing, and return the known signals at that point."
  ([]
   (part-1 input))
  ([instructions]
   (loop [known    {}
          resolved (reduce resolve-instruction known instructions)]
     (if (= known resolved)
       known
       (recur resolved
              (reduce resolve-instruction resolved instructions))))))

(defn part-2
  "Re-run the resolver with the modified instructions from part 2."
  []
  (let [revised (->> (io/resource "2015/day_7_v2.txt")
                     io/reader
                     line-seq
                     (map circuit)
                     (map second))]
    (part-1 revised)))
