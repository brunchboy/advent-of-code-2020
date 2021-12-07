(ns advent-of-code-2021.day-7-test
  "Unit tests for day 7."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-7 :as sut]))

(def sample-input
  [16,1,2,0,4,2,7,1,2,14])

(test/deftest part-1
  (test/is (= 37 (sut/part-1 sample-input)))
  (test/is (= 328187 (sut/part-1))))

(test/deftest part-2
  (test/is (= 168 (sut/part-2 sample-input)))
  (test/is (= 91257582 (sut/part-2))))
