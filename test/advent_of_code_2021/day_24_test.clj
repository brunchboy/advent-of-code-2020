(ns advent-of-code-2021.day-24-test
  "Unit tests for day 24."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-24 :as sut]))

(def sample-1
  "inp x
mul x -1")

(def sample-2
  "inp z
inp x
mul z 3
eql z x")

(test/deftest part-1
  (test/is (= {:inputs [], :registers {"x" 5}}
              (sut/execute {:inputs [5]} "inp x")))
  (test/is (= {:inputs [2], :registers {"x" -5}}
              (sut/run {:inputs [5 2]}
                sample-1)))
  (test/is (= {:inputs [], :registers {"z" 1, "x" 9}}
              (sut/run {:inputs [3 9]}
                sample-2)))
  (test/is (= {:inputs [], :registers {"z" 0, "x" 10}}
              (sut/run {:inputs [3 10]}
                sample-2))))
