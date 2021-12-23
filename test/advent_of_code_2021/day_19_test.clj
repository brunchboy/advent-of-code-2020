(ns advent-of-code-2021.day-19-test
    "Unit tests for day 19."
    (:require [clojure.test :as test]
              [advent-of-code-2021.day-19 :as sut]))

(def sample-data
  "The sample data."
  (sut/read-input "2021/day_19_sample.txt"))

(test/deftest part-1
  (test/is (= 79 (sut/part-1 sample-data)))
  (test/is (= 390 (sut/part-1))))

(test/deftest part-2
  (test/is (= 3621 (sut/part-2 sample-data)))
  (test/is (= 13327 (sut/part-2))))
