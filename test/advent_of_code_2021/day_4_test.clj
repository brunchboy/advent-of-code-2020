(ns advent-of-code-2021.day-4-test
  "Unit tests for day 4."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-4 :as sut]))


(def sample-calls
  "The numbers that are called in the sample input."
  [7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1])

(def sample-boards
  "The boards for the sample problem."
  (sut/build-boards [22 13 17 11  0
                     8  2 23  4 24
                     21  9 14 16  7
                     6 10  3 18  5
                     1 12 20 15 19

                     3 15  0  2 22
                     9 18 13 17  5
                     19  8  7 25 23
                     20 11 10 24  4
                     14 21 16 12  6

                     14 21 17 24  4
                     10 16 15  9 19
                     18  8 23 26 20
                     22 11 13  6  5
                     2  0 12  3  7]))

(test/deftest part-1
  (test/is (= 4512 (sut/part-1 sample-boards sample-calls)))
  (test/is (= 69579 (sut/part-1))))

(test/deftest part-2
  (test/is (= 1924 (sut/part-2 sample-boards sample-calls)))
  (test/is (= 14877 (sut/part-2))))
