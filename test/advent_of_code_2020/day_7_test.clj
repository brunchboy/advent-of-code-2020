(ns advent-of-code-2020.day-7-test
  "Unit tests for day 7."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-7 :as sut]))

(test/deftest part-1
  (test/is (= 155 (count (sut/part-1 "shiny gold")))))

(test/deftest part-2
  (test/is (= 54803 (sut/part-2 "shiny gold"))))
