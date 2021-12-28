(ns advent-of-code-2015.day-21-test
  "Unit tests for day 21."
  (:require [clojure.test :as test]
            [advent-of-code-2015.day-21 :as sut]))

(test/deftest part-1
  (test/is (= 121 (sut/part-1))))

(test/deftest part-2
  (test/is (= 201 (sut/part-2))))
