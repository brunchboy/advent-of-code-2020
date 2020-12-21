(ns advent-of-code-2020.day-21-test
  "Unit tests for day 21."
  (:require [clojure.string :as str]
            [clojure.test :as test]
            [advent-of-code-2020.day-21 :as sut]))

(def sample-input
  "The food list for the sample problem."
  (-> "mxmxvkd kfcds sqjhc nhms (contains dairy, fish)
trh fvjkl sbzzf mxmxvkd (contains dairy)
sqjhc fvjkl (contains soy)
sqjhc mxmxvkd sbzzf (contains fish)"
      (str/split-lines)))

(test/deftest part-1-sample
  (test/is (= (sut/part-1 sample-input) 5)))

(test/deftest part-1
  (test/is (= (sut/part-1) 1945)))

(test/deftest part-2-sample
  (test/is (= (sut/part-2 sample-input) "mxmxvkd,sqjhc,fvjkl")))

(test/deftest part-2
  (test/is (= (sut/part-2) "pgnpx,srmsh,ksdgk,dskjpq,nvbrx,khqsk,zbkbgp,xzb")))
