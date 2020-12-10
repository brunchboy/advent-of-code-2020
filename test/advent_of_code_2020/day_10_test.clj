(ns advent-of-code-2020.day-10-test
  "Unit tests for day 10."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-10 :as sut]))

(def sample-1
  "The first sample data."
  [16
   10
   15
   5
   1
   11
   7
   19
   6
   12
   4])

(def sample-2
  "The second sample data."
  [28
   33
   18
   42
   31
   14
   46
   20
   48
   47
   24
   23
   49
   45
   19
   38
   39
   11
   1
   32
   25
   35
   8
   17
   7
   9
   4
   2
   34
   10
   3])


(test/deftest part-1
  (test/is (= 1690 (sut/part-1 sut/input))))

(test/deftest part-2
  (test/is (= 8 (sut/part-2 sample-1)))
  (test/is (= 19208 (sut/part-2 sample-2)))
  (test/is (= 5289227976704 (sut/part-2 sut/input))))
