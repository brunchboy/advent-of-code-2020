(ns advent-of-code-2021.day-2-test
  "Unit tests for day 2."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-2 :as sut]))

(def sample-data "forward 5
down 5
forward 8
up 3
down 8
forward 2")

(test/deftest part-1
  (test/is (= 150 (sut/part-1 sample-data)))
  (test/is (= 1561344 (sut/part-1))))

(test/deftest part-2
  (test/is (= 900 (sut/part-2 sample-data)))
  (test/is (= 1848454425 (sut/part-2))))
