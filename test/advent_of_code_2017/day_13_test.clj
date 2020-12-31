(ns advent-of-code-2017.day-13-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-13 :as sut]
            [clojure.string :as str]))


(def sample-input
  "The firewall configuration for the sample problem."
  (->> "0: 3
1: 2
4: 4
6: 4"
       (str/split-lines)))

(test/deftest read-config-line
  (test/is (= [[0 3] [1 2] [4 4] [6 4]] (map sut/read-config-line sample-input))))

(test/deftest part-1-sample
  (test/is (= 24 (sut/severity sample-input))))

(test/deftest part-1
  (test/is (= 1844 (sut/severity sut/input))))

(test/deftest part-2-sample
  (test/is (= 10 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 3897604 (sut/part-2 sut/input))))
