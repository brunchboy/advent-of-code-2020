(ns advent-of-code-2017.day-17-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-17 :as sut]))

(test/deftest build-stepper
  (let [stepper (sut/build-stepper 3)]
    (test/is (= [[[0] 0]
                 [[0 1] 1]
                 [[0 2 1] 1]
                 [[0 2 3 1] 2]
                 [[0 2 4 3 1] 2]
                 [[0 5 2 4 3 1] 1]]
                (take 6 (iterate stepper [[0] 0]))))))

(test/deftest part-1-sample
  (test/is (= 638 (sut/part-1 3))))

(test/deftest part-1
  (test/is (= 2000 (sut/part-1 sut/input))))

(test/deftest part-2-sample
  (let [[buffer _pos] (nth (iterate (sut/build-stepper 3) [[0] 0]) 2017)]
    (test/is (= (second buffer)
                (sut/part-2 3 2017)))))

(test/deftest part-2
  (test/is (= 10242889 (sut/part-2 sut/input 50000000))))
