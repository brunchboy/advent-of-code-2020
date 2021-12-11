(ns advent-of-code-2021.day-11-test
  "Unit tests for day 11."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-11 :as sut]))

(def sample-input
  "The sample problem input."
  [5 4 8 3 1 4 3 2 2 3
   2 7 4 5 8 5 4 7 1 1
   5 2 6 4 5 5 6 1 7 3
   6 1 4 1 3 3 6 1 4 6
   6 3 5 7 3 8 5 4 7 8
   4 1 6 7 5 2 4 6 4 5
   2 1 7 6 8 4 1 7 2 1
   6 8 8 2 8 8 1 1 3 4
   4 8 4 6 8 4 8 5 5 4
   5 2 8 3 7 5 1 5 2 6])

(test/deftest part-1
  (test/is (= 1656 (sut/part-1 sample-input)))
  (test/is (= 1603 (sut/part-1))))

(test/deftest part-2
  (test/is (= 195 (sut/part-2 sample-input)))
  (test/is (= 222 (sut/part-2))))
