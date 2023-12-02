(ns advent-of-code-2023.day-1
  "Solutions for day 1"
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2023/day_1.txt")
       slurp
       str/split-lines))

(defn part-1
  "Solve part 1."
  []
  (->> input
       (map #(str/replace % #"[^0-9]" ""))
       (map #(str (first %) (last %)))
       (map parse-long)
       (apply +)))

(def sample
  "The sample data."
  (->> "two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"
       (str/split-lines)))

(defn translate-digits
  "Given a line of puzzle input, convert it to the list of digits that
  were originally there, according to the problem rules."
  [s]
  (loop [result ""
         left   s]
    (if (empty? left)
      result
      (cond
        (Character/isDigit (first left))
        (recur (str result (first left))
               (subs left 1))

        (str/starts-with? left "one")
        (recur (str result "1")
               (subs left 1))

        (str/starts-with? left "two")
        (recur (str result "2")
               (subs left 1))

        (str/starts-with? left "three")
        (recur (str result "3")
               (subs left 1))

        (str/starts-with? left "four")
        (recur (str result "4")
               (subs left 1))

        (str/starts-with? left "five")
        (recur (str result "5")
               (subs left 1))

        (str/starts-with? left "six")
        (recur (str result "6")
               (subs left 1))

        (str/starts-with? left "seven")
        (recur (str result "7")
               (subs left 1))

        (str/starts-with? left "eight")
        (recur (str result "8")
               (subs left 1))

        (str/starts-with? left "nine")
        (recur (str result "9")
               (subs left 1))

        :else (recur result
                     (subs left 1))))))

(defn part-2
  "Solve part 2."
  []
  (->> input
       (map translate-digits)
       (map #(str (first %) (last %)))
       (map parse-long)
       (apply +)))
