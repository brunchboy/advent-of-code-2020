(ns advent-of-code-2015.day-15
  "Solutions for day 15.")

(defn calculate-property
  "Given the map of ingredients and their properties per teaspoon, a map
  of ingredients to the number of teaspoons of that ingredient to be
  used, and an index into the properties (0 = capacity, 4 = calories),
  calculate the total of that property for this recipe, following the
  rules of the problem statament."
  [ingredients chosen index]
  (max 0 (apply + (for [[ingredient teaspoons] chosen]
                    (* teaspoons (nth (get ingredients ingredient) index))))))

(defn score
  "Calculate the total score for a particular recipe, given the map of
  ingredients and their properties per teaspoon, and a map of
  ingredients to the number of teaspoons of that ingredient to be
  used, calculate the total score for that recipe, following the rules
  of the problem statement."
  [ingredients chosen]
  (apply * (for [index (range 4)] (calculate-property ingredients chosen index))))

(def input
  "The puzzle input. Keys in this map are available ingredients, values
  are a vector of the properties per teaspoon of that ingredient, in
  the order capacity, durability, flavor, texture, and calories (not
  that the labels matter, apart from ignoring calories until part 2)."
  {"Sprinkles"    [5 -1 0 0 5]
   "PeanutButter" [-1 3 0 0 1]
   "Frosting"     [0 -1 4 0 6]
   "Sugar"        [-1 0 0 2 8]})

(defn part-1
  "Solve part 1 by brute-force enumerating all possible recipes that add
  up to 100 teaspoons, calculating the scores for each, and finding
  the best."
  []
  (let [all-cookies (for [sprinkles (range 101)
                          pb        (range (- 101 sprinkles))
                          frosting  (range (- 101 sprinkles pb))]
                      (let [chosen {"Sprinkles"    sprinkles
                                    "PeanutButter" pb
                                    "Frosting"     frosting
                                    "Sugar" (- 100 sprinkles pb frosting)}]
                        [(score input chosen) chosen]))]
    (reduce (fn [[score-1 chosen-1] [score-2 chosen-2]]
              (if (> score-1 score-2)
                [score-1 chosen-1]
                [score-2 chosen-2]))
            all-cookies)))

(defn part-2
  "Solve part 2 using the same approach as part 1, but filtering out
  any cookie recipes that do not add up to 500 calories."
  []
  (let [all-cookies (for [sprinkles (range 101)
                          pb        (range (- 101 sprinkles))
                          frosting  (range (- 101 sprinkles pb))]
                      (let [chosen {"Sprinkles"    sprinkles
                                    "PeanutButter" pb
                                    "Frosting"     frosting
                                    "Sugar" (- 100 sprinkles pb frosting)}]
                        [(score input chosen) chosen]))]
    (reduce (fn [[score-1 chosen-1] [score-2 chosen-2]]
              (if (> score-1 score-2)
                [score-1 chosen-1]
                [score-2 chosen-2]))
            (filter #(= 500 (calculate-property input (second %) 4)) all-cookies))))
