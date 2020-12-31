(ns advent-of-code-2017.day-10-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-10 :as sut]))

(test/deftest part-1-sample
  (test/is (= 12 (sut/part-1 (range 5) [3 4 1 5]))))

(test/deftest part-1
  (test/is (= 11413 (sut/part-1))))

(test/deftest build-lengths
  (test/is (= [49,44,50,44,51,17,31,73,47,23] (sut/build-lengths "1,2,3"))))

(test/deftest part-2-sample
  (test/is (= "a2582a3a0e66e6e86e3812dcb672a272" (sut/part-2 "")))
  (test/is (= "33efeb34ea91902bb2f59c9920caa6cd" (sut/part-2 "AoC 2017")))
  (test/is (= "3efbe78a8d82f29979031a4aa0b16a9d" (sut/part-2 "1,2,3")))
  (test/is (= "63960835bcdc130f0b66d7ff4f6a5a8e" (sut/part-2 "1,2,4"))))

(test/deftest part-2
  (test/is (= "7adfd64c2a03a4968cf708d1b7fd418d" (sut/part-2))))
