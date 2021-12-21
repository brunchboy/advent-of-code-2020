(ns advent-of-code-2021.day-21-test
  "Unit tests for day 21."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-21 :as sut]))

(test/deftest part-1
  (test/is (= 739785 (sut/part-1 4 8)))
  (test/is (= 675024 (sut/part-1))))

(test/deftest part-2
  (test/is (= 444356092776315 (sut/part-2 4 8)))
  (test/is (= 570239341223618 (sut/part-2))))
