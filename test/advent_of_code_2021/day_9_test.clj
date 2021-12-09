(ns advent-of-code-2021.day-9-test
  "Unit tests for day 9."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2021.day-9 :as sut]))

(def sample-input
  (->> "2199943210
3987894921
9856789892
8767896789
9899965678"
       str/split-lines
       (map (fn [line] (mapv #(Character/getNumericValue %) line)))
       vec))

(test/deftest part-1
  (test/is (= 15 (sut/part-1 sample-input)))
  (test/is (= 452 (sut/part-1))))

(test/deftest part-2
  (test/is (= 1134 (sut/part-2 sample-input)))
  (test/is (= 1263735 (sut/part-2))))
