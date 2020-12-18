(ns advent-of-code-2020.day-18-test
    "Unit tests for day 18."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-18 :as sut]))

(test/deftest part-1-samples
  (test/is (= 71 (sut/solve-homework-line "1 + 2 * 3 + 4 * 5 + 6")))
  (test/is (= 51 (sut/solve-homework-line "1 + (2 * 3) + (4 * (5 + 6))")))
  (test/is (= 26 (sut/solve-homework-line "2 * 3 + (4 * 5)")))
  (test/is (= 437 (sut/solve-homework-line "5 + (8 * 3 + 9 + 3 * 4 * 3)")))
  (test/is (= 12240 (sut/solve-homework-line "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))")))
  (test/is (= 13632 (sut/solve-homework-line "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))))

(test/deftest part-1
  (test/is (= 3348222486398 (sut/part-1))))

(test/deftest part-2-samples
  (test/is (= 231 (sut/solve-homework-line-2 "1 + 2 * 3 + 4 * 5 + 6")))
  (test/is (= 51 (sut/solve-homework-line-2 "1 + (2 * 3) + (4 * (5 + 6))")))
  (test/is (= 46 (sut/solve-homework-line-2 "2 * 3 + (4 * 5)")))
  (test/is (= 1445(sut/solve-homework-line-2 "5 + (8 * 3 + 9 + 3 * 4 * 3)")))
  (test/is (= 669060 (sut/solve-homework-line-2 "5 * 9 * (7 * 3 * 3 + 9 * 3 + (8 + 6 * 4))")))
  (test/is (= 23340 (sut/solve-homework-line-2 "((2 + 4 * 9) * (6 + 9 * 8 + 6) + 6) + 2 + 4 * 2"))))

(test/deftest part-2
  (test/is (= 43423343619505 (sut/part-2))))
