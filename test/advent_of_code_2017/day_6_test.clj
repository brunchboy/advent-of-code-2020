(ns advent-of-code-2017.day-6-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-6 :as sut]))

(def sample-input
  "The list of memory bank block counts in the sample problem."
  [0 2 7 0])

(test/deftest start-redistribution
  (test/is (= [[0 2 0 0] 7 2] (sut/start-redistribution sample-input))))

(test/deftest redistribution-step
  (test/is (= [[1 2 0 1] 5 0] (sut/redistribution-step [[0 2 0 1] 6 3]))))

(test/deftest run-cycle
  (test/is (= [2 4 1 2] (sut/run-redistribution-cycle sample-input))))

(test/deftest part-1-sample
  (test/is (= 5 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 7864 (sut/part-1 sut/input))))

(test/deftest part-2-sample
  (test/is (= 4 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 1695 (sut/part-2 sut/input))))
