(ns advent-of-code-2017.day-15-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-15 :as sut]))


(def sample-generator-a (sut/build-generator 16807 65))
(def sample-generator-b (sut/build-generator 48271 8921))

(test/deftest sample-generators
  (test/is (= [1092455 1181022009 245556042 1744312007 1352636452] (take 5 sample-generator-a)))
  (test/is (= [430625591 1233683848 1431495498 137874439 285222916] (take 5 sample-generator-b))))

(test/deftest sample-match
  (test/is (= [false false true false false]
              (take 5 (map sut/low-16-bits-match? sample-generator-a sample-generator-b)))))

(test/deftest part-1
  (test/is (= 600 (sut/part-1))))

(test/deftest part-2
  (test/is (= 313 (sut/part-2))))
