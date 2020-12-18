(ns advent-of-code-2020.day-18
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def input
  "The ticket notes (puzzle input)."
  (->> (io/resource "day_18.txt")
       io/reader
       line-seq))

(defn homework-eval-form
  "Once the expression has been parsed, evaluate it according to the
  part 1 precedence rules."
  [form]
  (if (sequential? form)
    (let [result (homework-eval-form (first form))
          op     (second form)]
      (case op
        nil
        result

        (let [next   (homework-eval-form (nth form 2))
              result ((eval op) result next)]
          (homework-eval-form (concat [result] (drop 3 form))))))
    form))


(defn solve-homework-line
  "Take advantage of Clojure's built-in EDN parser by turning the entire
  line into a single parenthetical expression and then parsing it into
  an EDN list, then feed that to our evaluator."
  [line]
  (homework-eval-form (edn/read-string (str "(" line ")"))))

(defn part-1
  "Solve part 1 by parsing and evaluating each line, then summing the
  results."
  []
  (apply + (map solve-homework-line input)))


(defn homework-eval-form-2
  "Once the expression has been parsed, evaluate it according to the
  strange part 2 precedence rules."
  [form]
  (if (sequential? form)
    (let [result (homework-eval-form-2 (first form))
          op     (second form)]
      (case op
        nil
        result

        +
        (let [next   (homework-eval-form-2 (nth form 2))
              result ((eval op) result next)]
          (homework-eval-form-2 (concat [result] (drop 3 form))))

        *
        ((eval op) result (homework-eval-form-2 (drop 2 form)))))
    form))

(defn solve-homework-line-2
  "Take advantage of Clojure's built-in EDN parser by turning the entire
  line into a single parenthetical expression and then parsing it into
  an EDN list, then feed that to our evaluator."
  [line]
  (homework-eval-form-2 (edn/read-string (str "(" line ")"))))

(defn part-2
  "Solve part 1 by parsing and evaluating each line, then summing the
  results."
  []
  (apply + (map solve-homework-line-2 input)))
