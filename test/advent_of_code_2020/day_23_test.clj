(ns advent-of-code-2020.day-23-test
    "Unit tests for day 23."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-23 :as sut]
            [advent-of-code-2020.day-23-old :as sut-old]))

(def sample-input
  "The cup arrangement from the sample problem."
  [3 8 9 1 2 5 4 6 7])

(test/deftest part-1-sample-old
  (test/is (= "67384529" (sut-old/part-1 sample-input))))

(test/deftest part-1-old
  (test/is (= "32658947" (sut-old/part-1))))

(test/deftest part-1-sample
  (test/is (= "67384529" (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= "32658947" (sut/part-1))))

(test/deftest part-2-sample
  (test/is (= 149245887792 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 683486010900 (sut/part-2))))
