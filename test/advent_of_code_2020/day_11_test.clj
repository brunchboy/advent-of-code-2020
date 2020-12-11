(ns advent-of-code-2020.day-11-test
  "Unit tests for day 11."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2020.day-11 :as sut]))

(def sample-data
  "The sample data in the problem statement."
  (str/split-lines
   "L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL"))

(test/deftest part-1-rules
  (test/is (= 37 (sut/part-1 sample-data))))

(test/deftest part-1
  (test/is (= 2275 (sut/part-1 sut/input))))

(test/deftest part-2-rules
  (test/is (= 26 (sut/part-2 sample-data))))

(test/deftest part-2
  (test/is (= 2121 (sut/part-2 sut/input))))
