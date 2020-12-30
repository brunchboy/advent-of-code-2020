(ns advent-of-code-2017.day-7
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The program descriptions (problem input)."
  (->> "2017/day_7.txt"
       io/resource
       io/reader
       line-seq))

(defn parse-program
  "Extracts the information from a line of the program structure report.
  Returns a tuple of the program name, its weight, and the set of
  names of programs it is directly supporting."
  [line]
  (let [[_ program weight _ supporting] (re-matches #"(\w+)\s+\((\d+)\)(\s+->\s+(.*))?" line)]
    [program (Long/parseLong weight) (set (when supporting (str/split supporting #",\s+")))]))

(defn find-parents
  "Processes the program description input into a map from program name
  to the set of parents supporting that program. Each entry should
  have only one parent, apart from the root, which will have none."
  [input]
  (reduce (fn [acc [program _ supporting]]
            (let [acc (update acc program (fnil set/union #{}) #{})]
              (reduce (fn [acc child]
                        (update acc child (fnil conj #{}) program))
                      acc
                      supporting)))
          {}
          (map parse-program input)))

(defn part-1
  "Solve part 1 of the problem by finding all program parents, and
  returning those who have none."
  [input]
  (filter (fn [[_ parents]]
            (empty? parents))
          (find-parents input)))

(defn program-map
  "Parse the input to produce a map from program names to tuples of the
  weight of that program and the set of children it supports."
  [input]
  (reduce (fn [acc [program weight supporting]]
            (assoc acc program [weight supporting]))
          {}
          (map parse-program input)))

(declare joint-weight-memo)

(defn joint-weight
  "Given a program map as build by `program-map` above, find the weight
  of a named program, recursively including the weights of the programs
  it supports."
  [programs program]
  (let [[weight supporting] (programs program)]
    (+ weight (apply + (map (partial joint-weight-memo programs) supporting)))))

(def joint-weight-memo
  "A memoized version of joint-weight to speed up calculations of
  program weight."
  (memoize joint-weight))

(defn recursive-program-map
  "Augments the map of program weight information to include the total
  weight at the program, along with the set of total weights of all
  children of that program, so we can find programs that are
  unbalanced (there will be more than one distinct child weight)."
  [input]
  (let [programs (program-map input)]
    (reduce (fn [acc [program [weight children]]]
              (assoc acc program {:weight weight
                                  :joint-weight (joint-weight-memo programs program)
                                  :children children
                                  :child-weights (set (map (partial joint-weight-memo programs) children))}))
            {}
            programs)))

(defn find-problem-programs
  "Given the recursively-filled-in map of program weights created above,
  list all the entries describing programs whose childen add up to
  different weights."
  [recursive-map]
  (filter (fn [[_ {:keys [child-weights]}]]
            (> (count child-weights) 1))
          recursive-map))

(defn find-lightest-problem-program
  "Find the program whose direct children are responsible for the
  balance problem. Since we know it can only be one program whose
  weight is wrong, it must be a child of the program with lightest
  joint weight that is unbalanced. Return that program's entry from
  the recursive program map."
  [recursive-map]
  (->> recursive-map
       find-problem-programs
       (sort-by (fn [[_ {:keys [joint-weight]}]]
                  joint-weight))
       first))

(defn analyze-weights
  "Given the frequency table of the weights of the children of the
  program where the problem has been found, return a tuple of the
  target child weight (the most common weight) and the problem child
  weight (the outlier). We perform sanity checks here to make sure
  there are only two different weight choices, and that one weight is
  unique, while the other is not."
  [weights]
  (assert (= (count weights) 2))
  (let [[[weight-1 frequency-1] [weight-2 frequency-2]] (seq weights)]
    (if (= frequency-1 1)
      (do (assert (> frequency-2 1))
          [weight-2 weight-1])
      (do (assert (= frequency-2 1))
          [weight-1 weight-2]))))

(defn part-2
  "Solve part 2 by finding the program at which the problem occurs,
  figuring out what the recursive weight each of its children should
  be, finding the program whose recursive weight is not that, and
  calculating what its own weight needs to be to make the recursive
  weight correct."
  [input]
  (let [recursive-map                  (recursive-program-map input)
        [_ {:keys [children]}]         (find-lightest-problem-program recursive-map)
        details                        (map recursive-map children)
        [target-weight problem-weight] (analyze-weights (frequencies (map :joint-weight details)))
        delta                          (- target-weight problem-weight)
        problem-child                  (first (filter #(= (:joint-weight %) problem-weight) details))]
    (+ (:weight problem-child) delta)))
