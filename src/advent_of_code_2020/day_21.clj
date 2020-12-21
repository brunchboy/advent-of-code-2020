(ns advent-of-code-2020.day-21
  "Solutions for day 21."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input "The food list (puzzle input)."
  (-> (io/reader (io/resource "day_21.txt"))
      line-seq))

(defn parse-line
  [line]
  (let [[_ ingredients allergens] (re-matches #"(.*)\s+\(contains\s+(.*)\)" line)]
    [(set (str/split ingredients #"\s+")) (set (str/split allergens #",\s+"))]))

(defn init-potentials
  [all-ingredients all-allergens]
  (reduce (fn [acc allergen]
            (assoc acc allergen all-ingredients))
          {}
          all-allergens))

(defn remove-ingredient-from-allergens
  [potentials ingredient allergens]
  (reduce (fn [acc allergen]
            (update acc allergen disj ingredient))
          potentials
          allergens))

(defn winnow
  [all-ingredients potentials [ingredient-set allergen-set]]
  (reduce (fn [acc ingredient]
             (if (ingredient-set ingredient)
               acc
               (remove-ingredient-from-allergens acc ingredient allergen-set)))
           potentials
           all-ingredients))

(defn part-1
  ([]
   (part-1 input))
  ([foods]
   (let [parsed          (map parse-line foods)
         ingredient-sets (map first parsed)
         allergen-sets   (map second parsed)
         all-ingredients (apply set/union ingredient-sets)
         all-allergens   (apply set/union allergen-sets)
         potentials      (reduce (partial winnow all-ingredients)
                                 (init-potentials all-ingredients all-allergens)
                                 parsed)
         safe            (set/difference all-ingredients (apply set/union (vals potentials)))]
     (->> ingredient-sets
          (map #(set/intersection safe %))
          (map count)
          (apply +)))))
