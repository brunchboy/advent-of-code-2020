(ns advent-of-code-2023.day-14
  "Solution for day 14."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(def sample-input
  "The example data."
  "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7")

(def input
  "The actual puzzle data."
  (-> (io/resource "2023/day_14.txt")
      slurp
      str/trim))

(defn elf-hash
  "Calculate the hash function specified for the problem."
  [s]
  (reduce (fn [acc c]
            (-> acc
                (+ (long c))
                (* 17)
                (mod 256)))
          0
          s))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (->> (str/split data #",")
        (map elf-hash)
        (reduce +))))

(defn remove-lens
  "Remove a lens with the matching label if it is present in a box."
  [coll label]
  (remove #(= (first %) label) coll))

(defn replace-lens
  "Replace the lens with the specified label, maintaining its position
  in the box, but updating the focal length. If not already present,
  put at the end of the box."
  [coll label focal-length]
  (loop [result []
         left   coll
         found  false]
    (if (empty? left)
      (if found
        result
        (conj result [label focal-length]))
      (let [current (first left)]
        (if (= label (first current))
          (recur (conj result [label focal-length])
                 (rest left)
                 true)
          (recur (conj result current)
                 (rest left)
                 found))))))

(defn step
  "Perform a single initialization step, parsing the lens and operation
  to be performed."
  [boxes s]
  (if-let [[_ label] (re-matches #"(\w+)-" s)]
    (update boxes (elf-hash label) (fnil remove-lens []) label)
    (let [[_ label focal-length] (re-matches #"(\w+)=(\d+)" s)]
      (update boxes (elf-hash label) (fnil replace-lens []) label focal-length))))

(defn calc-box-power
  "Calculate the focusing power of a single box, given the problem rules."
  [n lenses]
  (loop [result 0
         slot   1
         left   lenses]
    (if (empty? left)
      (* (inc n) result)
      (recur (+ result (* slot (parse-long (second (first left)))))
             (inc slot)
             (rest left)))))

(defn calc-power
  "Calculate the focusing power of all the boxes, given the problem rules."
  [boxes]
  (reduce-kv (fn [acc k v]
               (+ acc (calc-box-power k v)))
             0
             boxes))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (calc-power (reduce step {} (str/split data #",")))))
