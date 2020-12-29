(ns advent-of-code-2017.day-3-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-3 :as sut]))

(test/deftest part-1-samples
  (test/is (= 0 (sut/part-1 1)))
  (test/is (= 3 (sut/part-1 12)))
  (test/is (= 2 (sut/part-1 23)))
  (test/is (= 31 (sut/part-1 1024))))


(test/deftest part-1
  (test/is (= 475 (sut/part-1 277678))))

(test/deftest cell-sequence
  (test/is (= [{[0 0]   1,
                [2 -1]  26,
                [1 0]   1,
                [-1 0]  10,
                [1 1]   2,
                [-1 -1] 11,
                [1 -1]  25,
                [-1 1]  5,
                [0 -1]  23,
                [0 1]   4}
               11]
              (nth sut/cell-sequence 9))))

(test/deftest part-3
  (test/is (= 279138 (sut/part-2 277678))))
