(ns advent-of-code-2021.day-14-test
  "Unit tests for day 14."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-14 :as sut]))

(def sample-template
  "The seed for our polymer in the sample problem."
  "NNCB")

(def sample-rules
  "The insertion rules for the sample problem."
  {"CH" "B"
   "HH" "N"
   "CB" "H"
   "NH" "C"
   "HB" "C"
   "HC" "B"
   "HN" "C"
   "NN" "C"
   "BH" "H"
   "NC" "B"
   "NB" "B"
   "BN" "B"
   "BB" "N"
   "BC" "B"
   "CC" "N"
   "CN" "C"})

(test/deftest part-1
  (test/is (= 1588 (sut/part-1 sample-rules sample-template)))
  (test/is (= 2899 (sut/part-1))))

(test/deftest part-2
  (test/is (= 2188189693529 (sut/part-2 sample-rules sample-template)))
  (test/is (= 3528317079545 (sut/part-2))))
