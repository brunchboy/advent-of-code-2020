(ns advent-of-code-2021.day-1-test
  "Unit tests for day 1."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-1 :as sut]))

(def sample-data [199
                  200
                  208
                  210
                  200
                  207
                  240
                  269
                  260
                  263])

(test/deftest part-1
  (test/is (= 7 (sut/part-1 sample-data)))
  (test/is (= 1692 (sut/part-1))))

(test/deftest part-2
  (test/is (= 5 (sut/part-2 sample-data)))
  (test/is (= 1724 (sut/part-2))))
