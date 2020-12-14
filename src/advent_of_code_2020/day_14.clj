(ns advent-of-code-2020.day-14
  (:require [clojure.java.io :as io]
            [clojure.math.combinatorics :as combo]
            [clojure.string :as str]))

(def input
  "The initialization program (puzzle input)."
  (->> (io/resource "day_14.txt")
       io/reader
       line-seq))

(defn read-bitmask
  "Reads a bit mask string in the format specified by the problem
  statement, and sets it up for efficient use."
  [s]
  {:or  (Long/parseLong (str/replace s #"[^1]" "0") 2)
   :and (let [partial-mask (str/replace s #"[^0]" "1")]
          (Long/parseUnsignedLong (str (apply str (take (- 64 (count partial-mask)) (repeat "1"))) partial-mask)  2))})

(defn apply-mask
  "Applies the bit mask rules to a data value."
  [{:keys [mask]} val]
  (bit-and (:and mask) (bit-or (:or mask) val)))

(defn process
  "Handles a step of the initialization program. Either reads a new
  bitmask, or reads a memory assignment statment, and assigns the
  specified value (modified by the bit mask) to the specified
  address."
  [state step]
  (if-let [[_ mask] (re-matches #"mask\s+=\s+(.*)" step)]
    (assoc state :mask (read-bitmask mask))
    (if-let [[_ addr val] (re-matches #"mem\[(\d+)\]\s+=\s+(\d+)" step)]
      (update state :mem assoc (Long/parseLong addr) (apply-mask state (Long/parseLong val)))
      (throw (Exception. (str "Unrecognized instruction: " step))))))

(defn part-1
  "Applies all the processing steps in the problem input, then sums all
  the values that are left in memory at the end."
  []
  (->> (reduce process {:mask {:and -1
                               :or  0}
                        :mem  {}}
               input)
       :mem
       vals
       (apply +)))

(defn read-bitmask-2
  "Interprets the bit mask strings in according to the version 2
  specification in part 2, again setting up for efficient use. `:or`
  is all the bits that the mask will always set to 1. `:and` is the
  bits that do *not* float, so we start with all floating bits set to
  0. `:float` is the list of the place values of all floating bits,
  for easy subsetting."
  [s]
  {:or    (Long/parseLong (str/replace s #"[^1]" "0") 2)
   :and   (let [partial-mask (str/replace (str/replace s #"[^X]" "1") "X" "0")]
            (Long/parseUnsignedLong (str (apply str (take (- 64 (count partial-mask)) (repeat "1"))) partial-mask) 2))
   :float (loop [left   (reverse s)
                 place  1
                 result []]
            (if (seq left)
              (recur (rest left)
                     (* place 2)
                     (if (= (first left) \X)
                       (conj result place)
                       result))

              result))})

(defn expand-addresses
  "Given a bit mask and a base memory address, applies the bit mask and
  returns all actual memory addresses that should be affected, taking
  into account every possible value for the floating bits."
  [{:keys [mask] :as state} addr]
  (let [base (apply-mask state addr)]
    (map (fn [chosen]
           (bit-or base (apply + chosen)))
         (combo/subsets (:float mask)))))

(defn process-2
  "Handles a step of the initialization program as redefined in part 2.
  Either reads a new bit mask, or reads a memory address and value,
  expands the memory address to reflect the results of applying the
  bit mask, and assigns the value to each expanded address."
  [state step]
  (if-let [[_ mask] (re-matches #"mask\s+=\s+(.*)" step)]
    (assoc state :mask (read-bitmask-2 mask))
    (if-let [[_ addr val] (re-matches #"mem\[(\d+)\]\s+=\s+(\d+)" step)]
      (reduce (fn [state addr]
                (update state :mem assoc addr (Long/parseLong val)))
              state
              (expand-addresses state (Long/parseLong addr)))
      (throw (Exception. (str "Unrecognized instruction: " step))))))

(defn part-2
  "Applies all the processing steps in the problem input using the
  version 2 rules, then sums all the values that are left in memory at
  the end."  []
  (->> (reduce process-2 {:mask {:and   -1
                                 :or    0
                                 :float []}
                          :mem  {}}
               input)
       :mem
       vals
       (apply +)))
