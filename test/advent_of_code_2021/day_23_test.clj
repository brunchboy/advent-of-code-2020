(ns advent-of-code-2021.day-23-test
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-23 :as sut]))

(def sample-input
  "The sample problem to be solved."
  (sut/populate-rooms "BA" "CD" "BC" "DA"))

(def sample-input-2
  "The sample problem for part 2."
  (sut/populate-rooms "BDDA" "CCBD" "BBAC" "DACA"))

(test/deftest part-1
  (test/is (sut/needs-emptying? {:type :hall :content "A"}))
  (test/is (sut/needs-emptying? {:type :room :content "AB" :target "A"}))
  (test/is (not (sut/needs-emptying? {:type :room :content "AA" :target "A"})))
  (test/is (= 12521 (sut/solve sample-input))))

(test/deftest part-2
  (test/is (= 18195 (sut/solve sut/part-1-input)))
  (test/is (= 50265 (sut/solve sut/part-2-input))))
