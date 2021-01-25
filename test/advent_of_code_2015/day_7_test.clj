(ns advent-of-code-2015.day-7-test
  (:require [clojure.string :as str]
            [clojure.test :as test]
            [advent-of-code-2015.day-7 :as sut]))

(def sample-input
  "The wiring instructions for the sample problem."
  (->> "123 -> x
456 -> y
x AND y -> d
x OR y -> e
x LSHIFT 2 -> f
y RSHIFT 2 -> g
NOT x -> h
NOT y -> i"
       (str/split-lines)
       (map sut/circuit)
       (map second)))

(test/deftest part-1-sample
  (test/is (= {"d" 72
               "e" 507
               "f" 492
               "g" 114
               "h" 65412
               "i" 65079
               "x" 123
               "y" 456}
              (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 956 (get (sut/part-1) "a"))))


(test/deftest part-2
  (test/is (= 40149 (get (sut/part-2) "a"))))
