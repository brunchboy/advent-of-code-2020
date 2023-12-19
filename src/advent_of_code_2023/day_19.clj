(ns advent-of-code-2023.day-19
  "Solution for day 19."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [instaparse.core :as insta]))

(def sample-input
  "px{a<2006:qkq,m>2090:A,rfg}
pv{a>1716:R,A}
lnx{m>1548:A,A}
rfg{s<537:gd,x>2440:R,A}
qs{s>3448:A,lnx}
qkq{x<1416:A,crn}
crn{x>2662:A,R}
in{s<1351:px,qqz}
qqz{s>2770:qs,m<1801:hdj,R}
gd{a>3333:R,R}
hdj{m>838:A,pv}

{x=787,m=2655,a=1222,s=2876}
{x=1679,m=44,a=2067,s=496}
{x=2036,m=264,a=79,s=2244}
{x=2461,m=1339,a=466,s=291}
{x=2127,m=1623,a=2188,s=1013}")

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2023/day_19.txt")
       slurp))

(def instaparse-rules
  "Create a parser for the game list format."
  (insta/parser (io/resource "2023/day_19.bnf")))

(defn eval-rules
  "Turn the parsed rules into a map indexed by rule name for easy
  implementation."
  [rules]
  (reduce (fn [acc rule]
            (assoc acc (get-in rule [1 1]) (drop 2 rule)))
          {}
          rules))

(defn parse-part
  "Converts a part string into a Clojure map whose keys are the various
  attributes and the values are the rating for that attribute."
  [part]
  (-> (str/replace part "=" " ")
      edn/read-string))

(defn apply-comparison
  "Implements a comparison rule."
  [attribute comparison value part]
  (let [value     (parse-long value)
        attribute (symbol attribute)]
    (case comparison
      "<" (< (get part attribute) value)
      ">" (> (get part attribute) value))))

(defn apply-rule
  "Determines whether a rule applies to a part, and if so, returns the
  next destination for the part given the rule."
  [rule part]
  (let [rule (second rule)]
    (case (first rule)
      :NAME      (second rule)
      :REJECT    :reject
      :ACCEPT    :accept
      :CONDITION (let [[attribute comparison value destination] (rest rule)]
                   (when (apply-comparison attribute comparison value part)
                     (apply-rule [:RULE destination] part))))))

(defn apply-workflow
  "Determines the result of feeding a part to a workflow. Returns either
  a string identifying the name of the next rule to apply to the part,
  `:accept` if the part is accepted, or `:reject` if the part is
  rejected."
  [workflow part]
  (some #(apply-rule % part) workflow))

(defn apply-workflows
  "Runs a part through the workflows, and returns truthy if it is
  accepted."
  [workflows part]
  (loop [destination (apply-workflow (get workflows "in") part)]
    (case destination
      :accept destination
      :reject nil
      (recur (apply-workflow (get workflows destination) part)))))

(defn part-1
  "Solve part 1 of the puzzle."
  ([]
   (part-1 input))
  ([data]
   (let [[workflows parts] (->> data
                                str/split-lines
                                (partition-by empty?)
                                (take-nth 2))
         workflows         (->> (instaparse-rules (str/join "\n" workflows))
                                eval-rules)
         parts             (map parse-part parts)]
     (->> parts
            (filter (partial apply-workflows workflows))
            (mapcat vals)
            (reduce +)))))


(defn part-2
  "Solve part 2 of the puzzle."
  ([]
   (part-2 input))
  ([data]
))
