(ns advent-of-code-2020.day-22-test
  "Unit tests for day 22."
  (:require [clojure.string :as str]
            [clojure.test :as test]
            [advent-of-code-2020.day-22 :as sut]))

(def sample-input
  "The hands for the sample problem."
  (->> (str/split "Player 1:
9
2
6
3
1

Player 2:
5
8
4
7
10"
                  #"\n\n")
      (map sut/read-hand)))

(test/deftest part-1-sample
  (test/is (= (sut/part-1 sample-input) 306)))

(test/deftest part-1
  (test/is (= (sut/part-1) 31957)))

(test/deftest part-2-sample
  (test/is (= (sut/part-2 sample-input) 291)))

(test/deftest part-2
  (test/is (= (sut/part-2) 33212)))
