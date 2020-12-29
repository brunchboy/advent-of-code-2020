(ns advent-of-code-2017.day-5-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-5 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The list of jump offsets in the sample problem."
  (->> "0
3
0
1
-3"
       str/split-lines
       (mapv #(Long/parseLong %))))

(test/deftest part-1-sample
  (test/is (= 5 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 360603 (sut/part-1 sut/input))))

(test/deftest part-2-sample
  (test/is (= 10 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 25347697 (sut/part-2 sut/input))))
