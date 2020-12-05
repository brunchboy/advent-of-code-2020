(ns advent-of-code-2020.day-5-test
  "Unit tests for day 5."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-5 :as sut]))

(test/deftest example-boarding-passes
  (test/is (= 567 (sut/seat-code "BFFFBBFRRR")))
  (test/is (= 119 (sut/seat-code "FFFBBBFRRR")))
  (test/is (= 820 (sut/seat-code "BBFFBBFRLL"))))

(test/deftest part-1
  (test/is (= 858 (sut/part-1))))

(test/deftest part-2
  (test/is (= [557] (sut/part-2))))
