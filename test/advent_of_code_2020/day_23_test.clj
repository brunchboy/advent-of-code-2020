(ns advent-of-code-2020.day-23-test
    "Unit tests for day 23."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-23 :as sut]))

(def sample-input
  "The cup arrangement from the sample problem."
  [3 8 9 1 2 5 4 6 7])

(test/deftest part-1-sample
  (test/is (= "67384529" (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= "32658947" (sut/part-1))))
