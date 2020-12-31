(ns advent-of-code-2017.day-11-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-11 :as sut]))

(test/deftest part-1-samples
  (test/is (= 3 (sut/part-1 "ne,ne,ne")))
  (test/is (= 0 (sut/part-1 "ne,ne,sw,sw")))
  (test/is (= 2 (sut/part-1 "ne,ne,s,s")))
  (test/is (= 3 (sut/part-1 "se,sw,se,sw,sw"))))

(test/deftest part-1
  (test/is (= 818 (sut/part-1))))

(test/deftest part-2
  (test/is (= 1596 (sut/part-2))))
