(ns advent-of-code-2020.day-15-test
  "Unit tests for day 15."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-15 :as sut]))

(test/deftest sample-problems
  (test/is (= 436 (sut/part-1 [0 3 6])))
  (test/is (= 1 (sut/part-1 [1 3 2])))
  (test/is (= 10 (sut/part-1 [2 1 3])))
  (test/is (= 27 (sut/part-1 [1 2 3])))
  (test/is (= 78 (sut/part-1 [2 3 1])))
  (test/is (= 438 (sut/part-1 [3 2 1])))
  (test/is (= 1836 (sut/part-1 [3 1 2]))))


(test/deftest part-1
  (test/is (= 694 (sut/part-1))))

(test/deftest part-2
  (test/is (= 21768614 (sut/part-2))))
