(ns advent-of-code-2021.day-3
  "Solutions for day 3."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn ->matrix
  "Converts the input text to a matrix represented as vectors of vectors of numbers."
  [text]
  (->> text
      str/split-lines
      (map (fn [line] (vec (map #(- (int %) (int \0)) line))))
      vec))

(defn transpose
  "Transpose the rows and columns of a matrix."
  [matrix]
  (vec (apply map vector matrix)))

(defn find-most-common-bit
  "Returns a vector of the most common bit in each column of the input
  matrix."
  [v]
  (->> v
       frequencies
       (sort-by (comp - val))
       first
       first))

(defn find-gamma
  "Calculates the gamma value based on the puzzle rules, the most common
  bit from each column of the input data, returning the value as a
  string of 0 and 1 characters."
  [data]
  (->> data
       transpose
       (map find-most-common-bit)
       (apply str)))

(defn gamma->epsilon
  "Inverts the bits of a binary number represented as a string. Given a
  gamma string, this will return the epsilon value, according to the
  rules of part 1"
  [gamma]
  (->> gamma
       (map (fn [c] (if (= c \0) 1 0)))
       (apply str)))

(defn part-1
  "Solve part 1 of the problem."
  ([]
   (part-1 (->matrix (slurp (io/resource "2021/day_3.txt")))))
  ([data]
   (let [gamma (find-gamma data)
         epsilon (gamma->epsilon gamma)]
     (* (Long/parseLong gamma 2) (Long/parseLong epsilon 2)))))

;; Unfortunately, the above code was optimized for solving part one,
;; so I need somewhat more general implementations to solve part two.

(defn find-most-common-bit-2
  "Given a matrix of binary digits, find the most common digit in the
  column specified by index, with ties broken in favor of 1s."
  [matrix index]
  (let [freqs (->> matrix
                   (map (partial drop index))
                   (map first)
                   frequencies)
        zero  (get freqs 0)
        one   (get freqs 1)]
    (if (> zero one) 0 1)))

(defn find-least-common-bit-2
  "Given a matrix of binary digits, find the least common digit in the
  column specified by index, with ties broken in favor of 0s."
  [matrix index]
  (if (zero? (find-most-common-bit-2 matrix index)) 1 0))

(defn apply-bit-criteria
  "Filter a matrix of binary numbers by considering successive columns
  of bits, keeping entries where that column has either the most or
  least common value for the column (as determined by `rule`, until
  only one number remains. Return that."
  [matrix rule]
  (loop [index  0
         target (rule matrix index)
         matrix (vec (filter (fn [row] (= (nth row index) target)) matrix))]
    (if (< (count matrix) 2)
      (first matrix)
      (let [index  (inc index)
            target (rule matrix index)
            matrix (vec (filter (fn [row] (= (nth row index) target)) matrix))]
        (recur index target matrix)))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 (->matrix (slurp (io/resource "2021/day_3.txt")))))
  ([data]
   (let [o2  (apply str (apply-bit-criteria data find-most-common-bit-2))
         co2 (apply str (apply-bit-criteria data find-least-common-bit-2))]
     (* (Long/parseLong o2 2) (Long/parseLong co2 2)))))
