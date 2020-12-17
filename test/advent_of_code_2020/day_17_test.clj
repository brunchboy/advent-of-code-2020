(ns advent-of-code-2020.day-17-test
  "Unit tests for day 17."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2020.day-17 :as sut]))

(def sample-input
  "The simple initial state for the sample problem."
  (str/split-lines ".#.
..#
###"))

(test/deftest initial-state
  (let [cubes (sut/initial-state sample-input)]
    (test/is (= #{[1 0 0] [2 1 0] [0 2 0] [1 2 0] [2 2 0]} cubes))
    (test/is (= {:max   [2 2 0]
                 :min   [0 0 0]}
                (sut/cube-bounds cubes)))))

(test/deftest part-1-sample
  (test/is (= 112 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 395 (sut/part-1))))

(test/deftest part-2-sample
  (test/is (= 848 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 2296 (sut/part-2))))
