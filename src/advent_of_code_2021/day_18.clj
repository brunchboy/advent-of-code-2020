(ns advent-of-code-2021.day-18
  "Solutions for day 18."
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.math.combinatorics :as combi]))

(def input
  "The puzzle input."
  (-> (io/resource "2021/day_18.edn")
      slurp
      edn/read-string))

(defn add-right
  "Adds a value coming from the right hand side of an exploded snailfish
  number to the leftmost ordinary number in the `snailfish-number`."
  [snailfish-number value]
  (cond
    (zero? value)  ; Nothing to add, degenerate simple case.
    snailfish-number

    (number? snailfish-number)  ; We are adding to an ordinary number, base case.
    (+ snailfish-number value)

    :else  ; Still searching for first ordinary number on the left hand side of the snailfish number.
    [(add-right (first snailfish-number) value) (second snailfish-number)]))

(defn add-left
  "Adds a value coming from the left hand side of an exploded snailfish
  number to the rightmost ordinary number in the `snailfish-number`."
  [snailfish-number value]
  (cond
    (zero? value)  ; Nothing to add, degenerate simple case.
    snailfish-number

    (number? snailfish-number)  ; We are adding to an ordinary number, base case.
    (+ snailfish-number value)

    :else  ; Still searching for the first ordinary number on the left hand side of the snailfish number.
    [(first snailfish-number) (add-left (second snailfish-number) value)]))

(defn explode-internal
  "Recursive implementation of the explode algorithm, given a snailfish
  number, and the nesting level at which it exists."
  [snailfish-number nesting-level]
  (if (number? snailfish-number)
    [snailfish-number]  ; This is an ordinary number, nothing to explode.
    (if (> nesting-level 3)  ; We have reached the nesting level where we do need to explode.
      ;; Return a 0 to replace this pair, pull out the left and right values to propagate up, and flag explosion.
      [0 (first snailfish-number) (second snailfish-number) true]
      ;; We are still diving down to see if we need to explode anything; recursive case. Try right hand first.
      (let [[element left right exploded?] (explode-internal (first snailfish-number) (inc nesting-level))]
        (if exploded?  ; If we did explode our left, add the right hand value to our right, and propagate.
          [[element (add-right (second snailfish-number) right)] left 0 true]
          ; We did not explode on the right, try the left.
          (let [[element left right exploded?] (explode-internal (second snailfish-number) (inc nesting-level))]
            (if exploded?  ; If we did explode our right, add the left hand value to our right, and propagate.
              [[(add-left (first snailfish-number) left) element] 0 right true]
              [snailfish-number])))))))  ; We did not explode at all.

(defn explode
  "Outer wrapper for simply exploding a snailfish number if necessary."
  [snailfish-number]
  (first (explode-internal snailfish-number 0)))

(defn split-internal
  "Recursive implementation of the split algorithm."
  [snailfish-number]
  (if (number? snailfish-number)  ; Have we reached an ordinary number?
    (if (< snailfish-number 10)  ; Yes; is it big enough to split?
      [snailfish-number false]  ; No, we are not splitting.
      [[(quot snailfish-number 2) (quot (inc snailfish-number) 2)] true])  ; Yes, perform the split.
    ;; This is a pair, so first check if the left element needs to split.
    (let [[element split?] (split-internal (first snailfish-number))]
      (if split?
        ;; We did split on the left, so we are done looking. Perform the split and return with indicator we did.
        [[element (second snailfish-number)] true]
        ;; Did not yet split left element, so check the right element.
        (let [[element split?] (split-internal (second snailfish-number))]
          (if split?
            ;; We did split on the right, so perform the split and return with indicator we did.
            [[(first snailfish-number) element] true]
            ;; We did not split either element, so return original number with indicator it was unchanged.
            [snailfish-number false]))))))

(defn split
  "Outer wrapper for simply splitting a snailfish number if necessary."
  [snailfish-number]
  (first (split-internal snailfish-number)))


(defn reduce-snailfish-number
  "Repeatedly performs any explosions or splits necessary (prioritizing
  explosions over splits at each step) until no more changes to the
  number are needed."
  [snailfish-number]
  (loop [result snailfish-number]
    (let [[result exploded?] (explode-internal result 0)]
      (if exploded?
        (recur result)
        (let [[result split?] (split-internal result)]
          (if split?
            (recur result)
            result))))))

(defn add
  "Adds two snailfish numbers as specified in the problem statment.
  Forms a pair out of them, then reduces the resulting number."
  [sn-1 sn-2]
  (reduce-snailfish-number [sn-1 sn-2]))

(defn magnitude
  "Calculate the magnitude of a snailfish number as defined in the
  problem statement."
  [snailfish-number]
  (if (number? snailfish-number)
    snailfish-number
    (+ (* 3 (magnitude (first snailfish-number))) (* 2 (magnitude (second snailfish-number))))))

(defn part-1
  "Solve part 1: add a list of snailfush numbers, and calculate the
  magnitude of the result."
  ([]
   (part-1 input))
  ([snailfish-numbers]
   (magnitude (reduce add snailfish-numbers))))

(defn part-2
  "Solve part 2: find the largest magnitude of the result of adding any
  pair of numbers in the homework assignment. Since addition is not
  commutative, we need to try both orders."
  ([]
   (part-2 input))
  ([snailfish-numbers]
   (let [combinations (combi/combinations snailfish-numbers 2)]
     (->> (concat combinations (map reverse combinations))
          (map (fn [nums] (magnitude (apply add nums))))
          (apply max)))))
