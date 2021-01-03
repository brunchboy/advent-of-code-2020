(ns advent-of-code-2017.day-19-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-19 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The routing map for the sample problem."
  (-> "     |
     |  +--+
     A  |  C
 F---|----E|--+
     |  |  |  D
     +B-+  +--+
"
      str/split-lines
      vec))

(test/deftest part-1-sample
  (test/is (= "ABCDEF" (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= "VTWBPYAQFU" (sut/part-1 sut/input))))

(test/deftest part-2-sample
  (test/is (= 38 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 17358 (sut/part-2 sut/input))))
