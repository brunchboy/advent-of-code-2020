(ns advent-of-code-2017.day-23
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [advent-of-code-2017.day-18 :as day-18]))

(def input
  "The program instruction lines (puzzle input)."
  (-> "2017/day_23.txt"
      io/resource
      io/reader
      line-seq))

(defn execute
  "Given a current processor state, returns what the processor state
  will be after running the next line. `state` is a map whose keys
  include register names (holding the current value of that register),
  `:code` holding the lines of program code, and `:pc` holding the
  address of the next instruction to be executed. If the next
  instruction fell outside the program due to a jump, `:stop` becomes
  `true`. We also track the number of times the `mul` instruction is
  executed in `:mul`."
  [{:keys [pc code] :as state}]
  (let [[op dest arg] (str/split (get code pc) #"\s+")]
    (-> (case op
          "set" (assoc state dest (day-18/decode state arg))
          "sub" (update state dest (fnil - 0) (day-18/decode state arg))
          "mul" (-> state
                    (update dest (fnil * 0) (day-18/decode state arg))
                    (update :mul inc))
          "jnz" (cond-> state
                  (not (zero? (day-18/decode state dest)))
                  (update :pc + (dec (day-18/decode state arg)))))
        day-18/next-instruction)))

(defn run
  "Given a list of program lines, sets up the processor state to point
  at the first line and then returns a lazy sequence of the results of
  each processor cycle. If `debug` is false (not the default), starts
  out with a value of 1 in register `a`, to solve part 2."
  ([lines]
   (run lines true))
  ([lines debug]
   (iterate execute (merge
                     {:pc 0 :code (vec lines) :mul 0}
                     (when-not debug
                       {"a" 1})))))

(defn part-1
  "Solve part 1 of the problem. Run instructions until an `rcv`
  succeeds, and return what it recovered."
  [lines]
  (let [result (first (drop-while #(not (:stop %)) (run lines)))]
    (:mul result)))

;; Rather than actually running in non-debug mode, I simply used
;;  (def foo (advent-of-code-2017.day-23/run advent-of-code-2017.day-23/input false))
;; to set up a sequence of states I could analyze.
;;
;; From that, I determined that it was checking the numbers in the
;; range 108,400 through 125,400 stepping by 17 (inclusive) for
;; primality, and counting the ones that were composite. Since I
;; already had posted a fun exploration of how to efficiently generate
;; primes in Clojure,
;; https://github.com/Deep-Symmetry/afterglow/blob/master/doc/primes.md
;; I can simply use that to answer this question more efficiently.

(defn divides?
  "Returns true when the numerator can be evenly divided by the
  denominator."
  [numerator denominator]
  (zero? (rem numerator denominator)))

(defn sqrt-or-less?
  "Returns true when `candidate` is less than or equal to the
  square root of `n`."
  [n candidate]
  (<= candidate (Math/sqrt n)))

(declare primes)

(defn potential-prime-factors
  "Returns the prime numbers from 2 up to the square root of `n`."
  [n]
  (take-while (partial sqrt-or-less? n) primes))

(defn prime?
  "Returns true if `n` is prime."
  [n]
  (and (integer? n)
       (> n 1)
       (not-any? (partial divides? n) (potential-prime-factors n))))

(def primes
  "The prime numbers."
  (filter prime? (range)))

(defn part-2
  "Solve part 2, as described in the comment block above."
  []
  (let [lower      108400
        upper      125400
        candidates (set (range lower (inc upper) 17))  ; Take into account we are testing both ends of the range.
        prime (filter candidates (take-while #(<= % upper) (drop-while #(< % lower) primes)))]
    (- (count candidates) (count prime))))
