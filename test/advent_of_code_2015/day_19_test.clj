(ns advent-of-code-2015.day-19-test
  "Unit tests for day 19."
  (:require [clojure.test :as test]
            [advent-of-code-2015.day-19 :as sut]))

(def sample-data
  "The sample data, as a vector of replacements (from/to tuples)."
  [["H" "HO"]
   ["H" "OH"]
   ["O" "HH"]])

(test/deftest part-1
  (test/is (= 4 (sut/part-1 "HOH" sample-data)))
  (test/is (= 535 (sut/part-1))))

(def sample-data-2
  "The sample data for part 2, as a vector of replacements (from/to
  tuples)."
  [["e" "H"]
   ["e" "O"]
   ["H" "HO"]
   ["H" "OH"]
   ["O" "HH"]])

(test/deftest part-2
  (test/is (= 3 (sut/part-2 "HOH" sample-data-2)))
  (test/is (= 212 (sut/part-2-deeper))))
