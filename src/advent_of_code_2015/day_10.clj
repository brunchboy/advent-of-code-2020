(ns advent-of-code-2015.day-10)

(defn look-and-say
  "Transform a string of digits following the rules of the problem
  statement."
  [s]
  (let [runs (partition-by identity s)]
    (->> runs
         (map (fn [run]
                (str (count run) (first run))))
         (apply str))))


(defn part-1
  "Solve part 1."
  [input]
  (count (nth (iterate look-and-say input) 40)))

(defn part-2
  "Solve part 1."
  [input]
  (count (nth (iterate look-and-say input) 50)))
