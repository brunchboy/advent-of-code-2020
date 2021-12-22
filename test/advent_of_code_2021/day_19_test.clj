(ns advent-of-code-2021.day-19-test
    "Unit tests for day 19."
    (:require [clojure.test :as test]
              [clojure.set :as set]
              [advent-of-code-2021.day-19 :as sut]))

(def sample-data
  "The sample data."
  (sut/read-input "2021/day_19_sample.txt"))

(test/deftest part-1
  (test/is (= 79 (count (apply set/union (sut/normalize-sensors sample-data)))))
  (test/is (= 390 (count (apply set/union (sut/normalize-sensors sut/input))))))

(test/deftest part-2
  #_(test/is (= 3 (sut/part-2 "HOH" sample-data)))
  #_(test/is (= 212 (sut/part-2))))
