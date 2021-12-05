(ns advent-of-code-2015.day-17-test
  "Unit tests for day 17."
  (:require [clojure.test :as test]
            [advent-of-code-2015.day-17 :as sut]))

(def sample-data
  [{:id 1 :size 20} {:id 2 :size 15} {:id 3 :size 10} {:id 4 :size 5} {:id 5 :size 5}])

(test/deftest part-1
  (test/is (= 4 (sut/part-1 sample-data 25)))
  (test/is (= 1638 (sut/part-1))))

(test/deftest part-2
  (test/is (= 3 (sut/part-2 sample-data 25)))
  (test/is (= 17 (sut/part-2))))
