(ns advent-of-code-2020.day-12-test
  "Unit tests for day 12."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2020.day-12 :as sut]))

(def sample-data
  "The sample data in the problem statement."
  (str/split-lines
   "F10
N3
F7
R90
F11"))

(test/deftest part-1-rules
  (test/is (= 25 (sut/part-1 sample-data))))

(test/deftest part-1
  (test/is (= 2057 (sut/part-1))))

(test/deftest part-2-rules
  (test/is (= 286 (sut/part-2 sample-data))))

(test/deftest part-2
  (test/is (= 71504 (sut/part-2 sut/input))))
