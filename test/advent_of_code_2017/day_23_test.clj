(ns advent-of-code-2017.day-23-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-23 :as sut]))

(test/deftest part-1
  (test/is (= 6724 (sut/part-1 sut/input))))

(test/deftest part-2
  (test/is (= 903 (sut/part-2))))
