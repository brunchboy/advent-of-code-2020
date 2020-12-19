(ns advent-of-code-2020.day-19
  "Solutions for day 19."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [instaparse.core :as insta]))

;;; This first function is something I realized I could do shortly
;;; after actually getting my gold stars in the much more laborious
;;; way you will see later in the file.

(defn insta
  "No way! As I was brushing my teeth I realized that the problem rules
  had looked familiar because... they are valid input to instaparse, a
  Clojure parser generator. And it can handle both versions of the
  rules, including the recursive ones for part 2. So I could have had
  this done in five minutes, rather than the hours it took me! This
  code solves either part 1 or part 2, depending on the part number
  you call it with."
  ([]
   (insta 2))
  ([part]
   (let [data               (if (= 2 part) "day_19_2.txt" "day_19.txt")
         [grammar messages] (str/split (slurp (io/resource data)) #"\n\n")
         parser             (insta/parser grammar :start :0)]
     (->> messages
          str/split-lines
          (map parser)
          (remove :reason)
          count))))

;;; The code below is what I actually used to submit my solutions.

(def input
  "The list of rules and strings (the puzzle input)."
  (->> (io/resource "day_19.txt")
       io/reader
       line-seq))

(defn sections
  "Returns a sequence of each meaningful section of the rules, i.e.
  split at the blank lines."
  [coll]
  (->> coll
       (partition-by str/blank?)
       (take-nth 2)))

(defn simplify
  "Get rid of needless sets of parentheses in the generated regular
  expressions."
  [pattern]
  (if-let [match (re-matches #"(.*)\((\w+)\)(.*)" pattern)]
    (recur (apply str (rest match)))
    pattern))

(defn translate-to-regex
  "Convert a rule to the corresponding regular expression, which can be
  large."
  [raw-rules rule]
  (-> (apply str (map (fn [elem]
                        (cond (string? elem)
                              elem

                              (= '| elem)
                              "|"

                              (number? elem)
                              (str "(?:" ((memoize translate-to-regex) raw-rules (get raw-rules elem)) ")")

                              :else
                              (throw (Exception. (str "Unrecognized rule element: " elem)))))
                      rule))
      simplify
      re-pattern))

(defn parse-rules
  "Build a set of regular expressions corresponding to all the rules in
  the problem statement. (In retrospect, I only needed to do this for
  rule 0.)"
  [lines]
  (let [raw-rules (reduce (fn [acc line]
                            (if-let [[_ n rule] (re-matches #"(\d+):\s+(.*)" line)]
                              (let [n    (edn/read-string n)
                                    rule (edn/read-string (str "(" rule ")"))]
                                (assoc acc n rule))
                              (throw (Exception. (str "Malformed rule: " line)))))
                          {}
                          lines)]
    (reduce-kv (fn [acc n rule]
                 (assoc acc n (translate-to-regex raw-rules rule)))
               {}
               raw-rules)))

(defn part-1
  "Solve part 1."
  []
  (let [[rules messages] (sections input)
        regex            (get (parse-rules rules) 0)]
    (->> (filter #(re-matches regex %) messages)
         count)))

(defn hmm
  "Pull out the regular expressions of the rules we are going to change
  to examine them."
  []
  (let [[rules] (sections input)]
    (select-keys (parse-rules rules) [8 11])))


(defn adjust-regex-for-part-2
  "Adjusts the regular expression being built for rule 8 so that it
  corresponds to the version needed for part 2."
  [rule n]
  (case n
    8 (str rule "+")
    rule))

(defn translate-to-regex-2
  "Hacked version of the rule-to-regular-expression translator to tweak
  the results to work for part 2."
  [raw-rules rule n]
  (-> (apply str (map (fn [elem]
                        (cond (string? elem)
                              elem

                              (= '| elem)
                              "|"

                              (number? elem)
                              (str "(?:" ((memoize translate-to-regex-2) raw-rules (get raw-rules elem) elem) ")")

                              :else
                              (throw (Exception. (str "Unrecognized rule element: " elem)))))
                      rule))
      (adjust-regex-for-part-2 n)
      simplify
      re-pattern))

(defn adjust-rule-for-part-2
  "Adjust the text of rule 11 to correspond to enough expansions of the
  actual recursive rule that it matches all the messsages it needs to,
  before translating it to an even more huge regular expression."
  [rule n]
  (case n
    11 (str "42 31 | 42 42 31 31 | 42 42 42 31 31 31 | 42 42 42 42 31 31 31 31 | 42 42 42 42 42 31 31 31 31 31 | "
            "42 42 42 42 42 42 42 31 31 31 31 31 31 31 | 42 42 42 42 42 42 42 42 31 31 31 31 31 31 31 31")
    rule))

(defn parse-rules-2
  "Tweaked version of the rules parser to end up with a set of rules
  that work for part 2."
  [lines]
  (let [raw-rules (reduce (fn [acc line]
                            (if-let [[_ n rule] (re-matches #"(\d+):\s+(.*)" line)]
                              (let [n    (edn/read-string n)
                                    rule (edn/read-string (str "(" (adjust-rule-for-part-2 rule n) ")"))]
                                (assoc acc n rule))
                              (throw (Exception. (str "Malformed rule: " line)))))
                          {}
                          lines)]
    (reduce-kv (fn [acc n rule]
                 (assoc acc n (translate-to-regex-2 raw-rules rule n)))
               {}
               raw-rules)))

(defn part-2
  "Use the tweaked parser to get a regular expression that can solve
  part 2."
  []
  (let [[rules messages] (sections input)
        regex            (get (parse-rules-2 rules) 0)]
    (->> (filter #(re-matches regex %) messages)
         count)))
