(ns advent-of-code-2020.day-4
  "Solutions to the day 4 problems."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def data
  "The raw puzzle input for part 1."
  (slurp (io/resource "day_4.txt")))

(defn part-1
  "Count how many valid passports are present."
  []
  (let [candidates (map #(apply array-map (mapcat rest (re-seq #"(\S+):(\S+)\s*" %)))
                        (clojure.string/split data #"\n\n"))
        filtered (map #(select-keys % ["ecl" "pid" "eyr" "hcl" "byr" "iyr" "hgt"]) candidates)]
    (count (filter #(= 7 %) (map count filtered)))))

(defn valid?
  "Predicate which applies full suite of part 2 validation rules to a
  passport map."
  [p]
  (and (= 7 (count p))
       (let [byr (get p "byr")]
         (and (re-matches #"\d\d\d\d" byr)
              (<= 1920 (Long/parseLong byr) 2002)))
       (let [iyr (get p "iyr")]
         (and (re-matches #"\d\d\d\d" iyr)
              (<= 2010 (Long/parseLong iyr) 2020)))
       (let [eyr (get p "eyr")]
         (and (re-matches #"\d\d\d\d" eyr)
              (<= 2020 (Long/parseLong eyr) 2030)))
       (when-let [[_ hgt units] (re-matches #"(\d+)(in|cm)" (get p "hgt"))]
         (if (= units "cm")
           (<= 150 (Long/parseLong hgt) 193)
           (<= 59 (Long/parseLong hgt) 76)))
       (re-matches #"#[0-9a-f]{6}" (get p "hcl"))
       (#{"amb" "blu" "brn" "gry" "grn" "hzl" "oth"} (get p "ecl"))
       (re-matches #"\d{9}" (get p "pid"))))

(defn part-2
  "Count how many valid passports are present with stricter validation."
  []
  (let [candidates (map #(apply array-map (mapcat rest (re-seq #"(\S+):(\S+)\s*" %)))
                        (clojure.string/split data #"\n\n"))
        filtered (map #(select-keys % ["ecl" "pid" "eyr" "hcl" "byr" "iyr" "hgt"]) candidates)]
    (count (filter valid? filtered))))
