(ns advent-of-code-2021.day-15-test
  "Unit tests for day 15."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-15 :as sut]))

(def sample-data
  "1163751742
1381373672
2136511328
3694931569
7463417111
1319128137
1359912421
3125421639
1293138521
2311944581")

(def sample-graph
  (sut/build-graph sample-data))

(test/deftest part-1
  (test/is (= [[:n0-0
                :n0-1
                :n0-2
                :n1-2
                :n2-2
                :n3-2
                :n4-2
                :n5-2
                :n6-2
                :n6-3
                :n7-3
                :n7-4
                :n8-4
                :n8-5
                :n8-6
                :n8-7
                :n8-8
                :n9-8
                :n9-9]
               40]
              (sut/shortest-path sample-graph :n0-0 :n9-9)))
  (test/is (= 40 (sut/part-1 sample-graph 10 10)))
  (test/is (= 621 (sut/part-1))))

(def sample-graph-2
  (sut/build-graph-2 sample-data))

(test/deftest part-2
  (test/is (= {:n48-40 3, :n49-39 8, :n49-41 1} (get sample-graph-2 :n49-40)))
  (test/is (= 315 (sut/part-2 sample-graph-2 50 50)))
  (test/is (= 2904 (sut/part-2))))
