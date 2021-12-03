(ns advent-of-code-2021.day-3-test
  "Unit tests for day 3."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2021.day-3 :as sut]))

(def sample-data
  (sut/->matrix "00100
11110
10110
10111
10101
01111
00111
11100
10000
11001
00010
01010"))

(test/deftest part-1
  (test/is (= 198 (sut/part-1 sample-data)))
  (test/is (= 4174964 (sut/part-1))))

(test/deftest part-2
  (test/is (= 1 (sut/find-most-common-bit-2 sample-data 0)))
  (test/is (= [1 0 1 1 1] (sut/apply-bit-criteria sample-data sut/find-most-common-bit-2)))
  (test/is (= [0 1 0 1 0] (sut/apply-bit-criteria sample-data sut/find-least-common-bit-2)))
  (test/is (= 230 (sut/part-2 sample-data)))
  (test/is (= 4474944 (sut/part-2))))
