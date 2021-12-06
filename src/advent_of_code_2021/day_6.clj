(ns advent-of-code-2021.day-6
  "Solutions for day 6.")

(def input
  "The starting lanternfish population."
  [4 2 4 1 5 1 2 2 4 1 1 2 2 2 4 4 1 2 1 1 4 1 2 1 2 2 2 2 5 2 2 3 1 4 4 4 1 2 3 4 4 5 4 3 5 1 2 5 1 1
   5 5 1 4 4 5 1 3 1 4 5 5 5 4 1 2 3 4 2 1 2 1 2 2 1 5 5 1 1 1 1 5 2 2 2 4 2 4 2 4 2 1 2 1 2 4 2 4 1 3
   5 5 2 4 4 2 2 2 2 3 3 2 1 1 1 1 4 3 2 5 4 3 5 3 1 5 5 2 4 1 1 2 1 3 5 1 5 3 1 3 1 4 5 1 1 3 2 1 1 1
   5 2 1 2 4 2 3 3 2 3 5 1 5 1 2 1 5 2 4 1 2 4 4 1 5 1 1 5 2 2 5 5 3 1 2 2 1 1 4 1 5 4 5 5 2 2 1 1 2 5
   4 3 2 2 5 4 2 5 4 4 2 3 1 1 1 5 5 4 5 3 2 5 3 4 5 1 4 1 1 3 4 4 1 1 5 1 4 1 2 1 4 1 1 3 1 5 2 5 1 5
   2 5 2 5 4 1 1 4 4 2 3 1 5 2 5 1 5 2 1 1 1 2 1 1 1 4 4 5 4 4 1 4 2 2 2 5 3 2 4 4 5 5 1 1 1 1 3 1 2 1])

(defn step
  "Simulate one day in the life of the lanternfish population."
  [fish]
  (let [births (count (filter zero? fish))]
    (concat (map #(if (zero? %) 6 (dec %)) fish) (repeat births 8))))

(defn part-1
  "Solve part 1."
  []
  (count (last (take 81 (iterate step input)))))

(defn step-2
  "Simulate one day in the life of the lanternfish population without
  using all memory in the world, by tracking a frequency map of the
  fish timers, rather than a simple list."
  [fish]
  (let [births (get fish 0 0)]
    (reduce-kv (fn [m k v]
                 (assoc m
                        (if (zero? k) 6 (dec k))
                        (if (#{7 0} k) (+ (get fish 7 0) births) v)))
               {8 births}
               fish)))

(defn part-2
  "Solve part 2."
  []
  (apply + (vals (last (take 257 (iterate step-2 (frequencies input)))))))
