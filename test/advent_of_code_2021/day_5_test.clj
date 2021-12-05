(ns advent-of-code-2021.day-5-test
  "Unit tests for day 5."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-5 :as sut]))

(def sample-data
  "0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2")

(test/deftest part-1
  (test/is (= 5 (sut/count-bad-points sample-data)))
  (test/is (= 7085 (sut/part-1))))

(test/deftest part-2
  (test/is (= 12 (sut/count-bad-points-2 sample-data)))
  (test/is (= 20271 (sut/part-2))))
