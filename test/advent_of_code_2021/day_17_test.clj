(ns advent-of-code-2021.day-17-test
  "Unit tests for day 17."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-17 :as sut]))

(test/deftest part-1
  (test/is (sut/hits? [[20 30] [-10 -5]] [7 2]))
  (test/is (sut/hits? [[20 30] [-10 -5]] [6 3]))
  (test/is (sut/hits? [[20 30] [-10 -5]] [9 0]))
  (test/is (not (sut/hits? [[20 30] [-10 -5]] [17 -4])))
  (test/is (= (sut/part-1 [[20 30] [-10 -5]]) 45))
  (test/is (= 6903 (sut/part-1))))

(test/deftest part-2
  (test/is (= 112 (sut/part-2 [[20 30] [-10 -5]])))
  (test/is (= 2351 (sut/part-2))))
