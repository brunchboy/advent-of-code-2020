(ns advent-of-code-2020.day-2
  "Solutions to the day 2 problems."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def passwords
  (->> (slurp (io/resource "day_2.txt"))
      str/split-lines
      (map #(str/split % #"\s*:\s*"))))

(defn valid-1
  "Check whether a password meets its validity requirements, per the
  rules for part 1."
  [[policy pw]]
  (let [[_ min-count max-count c] (re-matches #"(\d+)-(\d+)\s+(\w)" policy)
        found                     (count (re-seq (re-pattern c) pw))
        min-count                 (Long/parseLong min-count)
        max-count                 (Long/parseLong max-count)]
    (<= min-count found max-count)))

(defn part-1
  "Return the passwords valid for part 1 (count the result to solve the
  puzzle)."
  []
  (filter valid-1 passwords))

(defn valid-2
  "Check whether a password meets its validity requirements, per the
  rules for part 2."
  [[policy pw]]
  (let [[_ pos-1 pos-2 c] (re-matches #"(\d+)-(\d+)\s+(\w)" policy)
        pos-1                 (Long/parseLong pos-1)
        pos-2                 (Long/parseLong pos-2)
        match-1 (if (= (str (nth pw (dec pos-1))) c) 1 0)
        match-2 (if (= (str (nth pw (dec pos-2))) c) 1 0)]
    (= 1 (+ match-1 match-2))))

(defn part-2
  "Return the passwords valid for part 2 (count the result to solve the
  puzzle)."
  []
  (filter valid-2 passwords))
