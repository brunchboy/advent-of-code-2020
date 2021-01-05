(ns advent-of-code-2017.day-24-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-24 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The component list from the sample problem."
  (-> "0/2
2/2
2/3
3/4
3/5
0/1
10/1
9/10"
      str/split-lines))

(test/deftest other-port
  (test/is (= 2 (sut/other-port [1 2] 1)))
  (test/is (= 1 (sut/other-port [1 2] 2)))
  (test/is (= 2 (sut/other-port [2 2] 2))))

(test/deftest valid-bridge
  (test/is (sut/valid-bridge? [[0 3]]))
  (test/is (not (sut/valid-bridge? [[1 3]])))
  (test/is (sut/valid-bridge? [[0 3] [4 3]]))
  (test/is (not (sut/valid-bridge? [[0 3] [4 5]])))
  (test/is (sut/valid-bridge? [[0 3] [4 3] [4 5]]))
  (test/is (not (sut/valid-bridge? [[0 3] [4 3] [3 5]]))))

(test/deftest all-bridges-sample
  (test/is (= '(([0 2])
                ([0 1])
                ([0 2] [2 2])
                ([0 2] [2 3])
                ([0 1] [10 1])
                ([0 2] [2 2] [2 3])
                ([0 2] [2 3] [3 4])
                ([0 2] [2 3] [3 5])
                ([0 1] [10 1] [9 10])
                ([0 2] [2 2] [2 3] [3 4])
                ([0 2] [2 2] [2 3] [3 5]))
              (sut/all-bridges sample-input))))

(test/deftest slow-part-1-sample
  (test/is (= 31 (sut/slow-part-1 sample-input))))

(test/deftest next-options
  (test/is (= [[[0 1] '([3 5] [3 4] [2 3] [2 2] [0 2] [10 1] [9 10])]
               [[0 2] '([2 2] [2 3] [3 4] [3 5] [0 1] [10 1] [9 10])]]
              (sut/next-options (map sut/read-component sample-input) 0))))

(test/deftest longest-bridges-sample
  (test/is (= [[[0 1] [10 1] [9 10]]
               [[0 2] [2 3] [3 5]]
               [[0 2] [2 3] [3 4]]
               [[0 2] [2 2] [2 3] [3 5]]
               [[0 2] [2 2] [2 3] [3 4]]]
              (sut/longest-bridges [] 0 (map sut/read-component sample-input)))))

(test/deftest part-1-sample
  (test/is (= 31 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 1868 (sut/part-1 sut/input))))

(test/deftest part-2-sample
  (test/is (= 19 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 1841 (sut/part-2 sut/input))))
