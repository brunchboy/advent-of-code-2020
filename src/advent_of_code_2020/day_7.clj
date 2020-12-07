(ns advent-of-code-2020.day-7
  "Solutions to the day 7 problems."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def rules
  "The bag hierarchy rules (the puzzle input)."
  (->> (io/resource "day_7.txt")
       io/reader
       line-seq))

(defn parse-contents
  "Produce a list of tuples of the number and bag type from a
  containment rule."
  [holds]
  (map (fn [entry]
         (if-let [[_ n bag-type] (re-matches #"(\d+)\s+(.*)\s+bags?" entry)]
           [n bag-type]
           (throw (Exception. (str "Unable to parse contents entry:" entry)))))
       (str/split holds #",\s+")))

(defn parse-container-rule
  "Parse a rule into a structure helpful for solving part 1, updating
  the containers map accordingly. (See load-rules below for what it
  ends up becoming.)"
  [containers rule]
  (if-let [[_ bag-type holds] (re-matches #"(.*)\s+bags contain\s+(.*)\." rule)]
    (if (= "no other bags" holds)
      containers  ; This rule doesn't add a new container relationship.
      (reduce (fn [acc [n content-type]]
                (update acc content-type (fnil assoc {}) bag-type n))
              containers
              (parse-contents holds)))
    (throw (Exception. (str "Can't parse rule: " rule)))))

(defn load-rules
  "Set up the container relationships needed for solving part 1 by
  parsing the rules. Returns a map from bag type to a map whose keys
  are the bag type that can hold this type of bag, and whose values
  are the number of this type of bag that can be held in that holder
  type. that holds them."
  []
  (reduce parse-container-rule {} rules))

(defn part-1
  "Count the number of bag types which can eventually contain the
  specified bag type."
  ([bag-type]
   (part-1 bag-type (load-rules) #{}))
  ([bag-type containers counted]
   (let [direct (set (keys (containers bag-type)))]
     (apply set/union direct (map #(part-1 % containers (set/union direct counted))
                                  (set/difference direct counted))))))

(defn parse-content-rule
  "Parse a rule into a structure helpful for solving part 2, updating
  the containers map accordingly. (See load-rules-2 below for what it
  becomes.)"
  [containers rule]
  (if-let [[_ bag-type holds] (re-matches #"(.*)\s+bags contain\s+(.*)\." rule)]
    (if (containers bag-type)
      (throw (Exception. (str "Already have a rule for bag type: " bag-type)))
      (if (= "no other bags" holds)
        (assoc containers bag-type [])
        (assoc containers bag-type (parse-contents holds))))
    (throw (Exception. (str "Can't parse rule: " rule)))))


(defn load-rules-2
  "Set up the simpler container relationships needed for part 2 by
  parsing the rules. Return a map from bag type to a list of tuples of
  the numbers and bag types that it must contain."
  []
  (reduce parse-content-rule {} rules))

(defn part-2
  "Count the number of bags that the specified bag type must contain."
  ([bag-type]
   (part-2 bag-type (load-rules-2)))
  ([bag-type containers]
   (apply + (map (fn [[n inner-type]]
                   (let [n (Long/parseLong n)]
                     (+ n (* n (part-2 inner-type containers)))))
                 (containers bag-type)))))
