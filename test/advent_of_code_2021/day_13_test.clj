(ns advent-of-code-2021.day-13-test
  "Unit tests for day 13."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2021.day-13 :as sut]))

(def sample-input
  "The input for the sample problem."
  "6,10
0,14
9,10
0,3
10,4
4,11
6,0
6,12
4,1
0,13
10,12
3,4
3,0
8,4
1,10
2,14
8,10
9,0

fold along y=7
fold along x=5")

(test/deftest part-1
  (let [[dots _ folds] (->> sample-input str/split-lines (partition-by str/blank?))
         dots (sut/build-dot-set dots)]
    (test/is (= 17 (count (sut/apply-fold dots (first folds)))))))

;; This one was not conducive to unit tests for the actual answer, since I was printing it rather than returning it.
;; Oh well.
