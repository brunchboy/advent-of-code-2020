(ns advent-of-code-2020.day-21
  "Solutions for day 21."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input "The food list (puzzle input)."
  (-> (io/reader (io/resource "day_21.txt"))
      line-seq))

(defn parse-line
  "Converts a line of the puzzle statement to a tuple of the set of
  ingredients mentioned on it, and the set of allergens they contain."
  [line]
  (let [[_ ingredients allergens] (re-matches #"(.*)\s+\(contains\s+(.*)\)" line)]
    [(set (str/split ingredients #"\s+")) (set (str/split allergens #",\s+"))]))

(defn init-potentials
  "Builds the intial map of potential allergens for ingredients, in
  which we start with every ingredient mapping to a set of all known
  allergens."
  [all-ingredients all-allergens]
  (reduce (fn [acc allergen]
            (assoc acc allergen all-ingredients))
          {}
          all-allergens))

(defn remove-ingredient-from-allergens
  "Once we have determined that an ingredient cannot contain any
  allergens, this function removes that ingredient from the sets of
  potential ingredients for each allergen. Returns the modified map of
  allergens to potential ingredients."
  [potentials ingredient allergens]
  (reduce (fn [acc allergen]
            (update acc allergen disj ingredient))
          potentials
          allergens))

(defn winnow
  "Given the list of all ingredients, a map of allergies to sets of
  potential ingredients containing them, and an ingredient
  statement (a set of ingredients and the set of allergens they may
  contain), if it determines that this rule means an ingredient cannot
  contain an allergen (because that ingredient is missing from the set
  of ingredients which might contain that allergen), removes that
  ingredient from the set of potential ingredients for all allergens.
  Returns the resulting simplified map."
  [all-ingredients potentials [ingredient-set allergen-set]]
  (reduce (fn [acc ingredient]
             (if (ingredient-set ingredient)
               acc
               (remove-ingredient-from-allergens acc ingredient allergen-set)))
           potentials
           all-ingredients))

(defn part-1
  "Solves part 1 of the problem. Parses the ingredient statement, builds
  sets of all ingredients and allergens, then builds the map of all
  potential ingredients that contain each allergen, removing
  ingredients which cannot possibly contain any allergen. Uses that to
  find the `safe` set of ingredients, which contain no allergens.
  Finally, counts how many times those safe ingredients occur in the
  ingredients list, and returns that count."
  ([]
   (part-1 input))
  ([foods]
   (let [parsed          (map parse-line foods)
         ingredient-sets (map first parsed)  ; The first halves of each ingredient statement
         allergen-sets   (map second parsed) ; The second halves of each ingredient statement
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

(defn remove-elsewhere-if-singleton
  "Given an allergen and a map of allergies to sets of potential
  ingredients containing them, checks whether there is only one such
  ingredient for the specified allergen. If so, updates the candidate
  map to remove that ingredient from every other allergen."
  [allergen potentials]
  (let [current (get potentials allergen)]
    (if (= (count current) 1)
      (reduce-kv (fn [acc k v]
                   (if (= k allergen)
                     (assoc acc k v)
                     (assoc acc k (set/difference v current))))
                    {}
                    potentials)
      potentials)))

(defn resolve-singletons
  "Given a map of allergens to sets of potential ingredients containing
  them, improves our understanding of the relationships by removign
  any allergens whose sole ingredient has been identified from the
  other allergen maps."
  [potentials]
  (loop [result    potentials
         allergens (keys potentials)]
    (if-let [current (first allergens)]
      (recur (remove-elsewhere-if-singleton current result)
             (rest allergens))
      result)))

(defn narrow-to-fixed-point
  "Given a map of allergens to sets of potential ingredients containing
  them, repeatedly simplifies by resolving ambiguities via
  `resolve-singletons` until that produces no further improvement.
  Returns the resulting map."
  [potentials]
  (reduce (fn [previous current]
            (if (= previous current)
              (reduced current)
              current))
          {}
          (iterate resolve-singletons potentials)))

(defn part-2
  "Solves part 1 of the problem. Parses the ingredient statement, builds
  sets of all ingredients and allergens, then builds the map of all
  potential ingredients that contain each allergen, removing
  ingredients which cannot possibly contain any allergen. Repeatedly
  narrows that by resolving ambiguities until we have a unique
  assignment of ingredients to allergens. Sorts the results by
  allergen, then returns a comma-separated list of their corresponding
  ingredients."
  ([]
   (part-2 input))
  ([foods]
   (let [parsed          (map parse-line foods)
         ingredient-sets (map first parsed)
         allergen-sets   (map second parsed)
         all-ingredients (apply set/union ingredient-sets)
         all-allergens   (apply set/union allergen-sets)
         potentials      (reduce (partial winnow all-ingredients)
                                 (init-potentials all-ingredients all-allergens)
                                 parsed)
         narrowed        (narrow-to-fixed-point potentials)]
     (->> narrowed
          (sort-by first)
          (map second)
          (apply concat)
          (str/join ",")))))
