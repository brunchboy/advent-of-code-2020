(ns advent-of-code-2015.day-11
  (:require [clojure.string :as str]))

(defn is-straight?
  "Checks whether a sequence of characters is monotonically increasing
  by one."
  [s]
  (->> s
       (partition 2 1)
       (map (fn [[a b]] (- (int b) (int a))))
       (every? #(= 1 %))))

(defn has-straight?
  "Checks if the string has at least one sequence of three characters
  which increase in ASCII value by one each."
  [s]
  (->> s
       (partition 3 1)
       (filter is-straight?)
       seq))

(defn free-of-iol?
  "Checks that the string contains none of the characters i, o, and l."
  [s]
  (not (or (str/includes? s "i")
           (str/includes? s "o")
           (str/includes? s "l"))))

(defn has-multiple-pairs?
  "Checks whether the string has more than one pair of repeated
  letters (involving different letters)."
  [s]
  (let [pairs (->> s
                   (partition 2 1)
                   (filter (fn [[a b]] (= a b)))
                   set)]
    (> (count pairs) 1)))

(defn valid?
  "Checks if a password is valid, according to the rules of the
  problem."
  [s]
  (and (has-straight? s)
       (free-of-iol? s)
       (has-multiple-pairs? s)))

(defn increment
  "Finds the next password to test, as if the string was an odometer of
  lowercase letters that is advancing one unit."
  [s]
  (let [end (last s)]
    (if (= end \z)
      (apply str (concat (increment (butlast s)) [\a]))
      (apply str (concat (butlast s) [(char (inc (int end)))])))))

(defn part-1
  "Find the next valid password after the one supplied."
  [s]
  (first (filter valid? (rest (iterate increment s)))))
