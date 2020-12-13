(ns advent-of-code-2020.day-13
  "Solutions for day 13"
  (:require [clojure.math.numeric-tower :as math]
            [clojure.string :as str]))

(def input
  "The bus schedule (puzzle input)."
  (str/split "17,x,x,x,x,x,x,x,x,x,x,37,x,x,x,x,x,907,x,x,x,x,x,x,x,x,x,x,x,19,x,x,x,x,x,x,x,x,x,x,23,x,x,x,x,x,29,x,653,x,x,x,x,x,x,x,x,x,41,x,x,13"
                      #","))

(defn time-until-bus
  "Given the timestamp at which we arrived at the bus stop, and a bus
  ID, return the number of minutes we need to wait until that bus next
  arrives."
  [timestamp id]
  (- id (mod timestamp id)))

(defn best-bus
  "Given the timestamp at which we arrived at the bus stup, and the list
  of bus IDs that are in service, return a tuple of the ID of the
  first bus that will arrive, and the number of minutes we will need
  to wait for it."
  [timestamp ids]
  (let [with-waits (for [id ids]
                     [id (time-until-bus timestamp id)])]
    (apply min-key second with-waits)))

(defn part-1
  "Solve part 1 of the problem: Multiply the ID of the first bus that
  will arrive by the number of minutes we will need to wait for it.
  Can also take a timestamp and bus ID list as arguments in order to
  validate that the sample data is handled correctly."
  ([]
   (let [ids (->> input
                  (remove #(= "x" %))
                  (map #(Long/parseLong %)))]
     (part-1 1000186 ids)))
  ([timestamp ids]
   (apply * (best-bus timestamp ids))))

(defn extended-gcd
  "The extended Euclidean algorithm, from Rosetta Code. Returns a list
  containing the GCD and the Bézout coefficients corresponding to the
  inputs. "
  [a b]
  (cond (zero? a) [(math/abs b) 0 1]
        (zero? b) [(math/abs a) 1 0]
        :else     (loop [s  0
                         s0 1
                         t  1
                         t0 0
                         r  (math/abs b)
                         r0 (math/abs a)]
                    (if (zero? r)
                      [r0 s0 t0]
                      (let [q (quot r0 r)]
                        (recur (- s0 (* q s)) s
                               (- t0 (* q t)) t
                               (- r0 (* q r)) r))))))

 (defn chinese-remainder
  "Calculate the chinese remainder theorem for the list of primes in `n`
  and the list of remainders in `a`. Based on the Rosetta Code solution."
  [n a]
   (let [prod     (apply * n)
         reducer  (fn [sum [n_i a_i]]
                    (let [p     (quot prod n_i)
                          egcd  (extended-gcd p n_i)
                          inv_p (second egcd)]  ; Second element returned is the inverse.
                      (+ sum (* a_i inv_p p))))
         sum-prod (reduce reducer 0 (map vector n a))]
    (mod sum-prod prod)))

(defn remainder-parameters
  "Convert the bus schedule to the corresponding parameters for the
  Chinese Remainder Theorem which will produce the earliest timestamp
  at which the buses will arrive at the intervals specified in the
  problem statement."
  [schedule]
  (->> (map-indexed (fn [i id]
                      (when (not= id "x")
                        [(Long/parseLong id) (- i)]))
                    schedule)
       (filter identity)
       (apply map vector)))

(defn part-2
  "Solve part 2 of the problem: Return the earliest timestamp at which
  the buses will arrive at the intervals derived from the schedule as
  described in the problem statement. Can also take a timestamp and
  bus schedule list as arguments in order to validate that the example
  data is computed correctly."
  ([]
   (part-2 input))
  ([buses]
   (apply chinese-remainder (remainder-parameters buses))))
