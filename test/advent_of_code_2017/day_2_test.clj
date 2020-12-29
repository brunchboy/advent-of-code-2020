(ns advent-of-code-2017.day-2-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-2 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The spreadsheet used in the sample problems."
  (str/split-lines "5 1 9 5
7 5 3
2 4 6 8"))

(test/deftest checksum-line
  (test/is (= [8 4 6] (map sut/checksum-line sample-input))))

(test/deftest part-1-sample
  (test/is (= 18 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 41919 (sut/part-1 sut/input))))

(def sample-input-2
  "The spreadsheet used in the part 2 sample problem."
  (str/split-lines "5 9 2 8
9 4 7 3
3 8 6 5"))

(test/deftest find-divisibles
  (test/is (= [[8 2] [9 3] [6 3]] (map sut/find-divisibles sample-input-2))))

(test/deftest part-2-sample
  (test/is (= 9 (sut/part-2 sample-input-2))))

(test/deftest part-2
  (test/is (= 303 (sut/part-2 sut/input))))
