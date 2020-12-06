(ns advent-of-code-2020.day-6-test
  "Unit tests for day 6."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-6 :as sut]))

(test/deftest part-1
  (test/is (= 6809 (sut/part-1))))

(test/deftest part-2
  (test/is (= 3394 (sut/part-2))))
