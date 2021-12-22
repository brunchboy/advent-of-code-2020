(ns advent-of-code-2021.day-22-test
  "Unit tests for day 22."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-22 :as sut]))

(def sample-input
  (sut/read-input "2021/day_22_sample.txt"))

(test/deftest part-1
  (test/is (= 590784 (sut/part-1 sample-input)))
  (test/is (= 545118 (sut/part-1))))
