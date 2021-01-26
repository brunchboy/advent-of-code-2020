(ns advent-of-code-2015.day-9-test
  (:require [clojure.string :as str]
            [clojure.test :as test]
            [advent-of-code-2015.day-9 :as sut]))

(def sample-input
  "The sample list of city pair distances."
  (-> "London to Dublin = 464
London to Belfast = 518
Dublin to Belfast = 141"
      str/split-lines))

(test/deftest read-distances
  (test/is (= {#{"London" "Dublin"} 464,
               #{"Belfast" "London"} 518,
               #{"Belfast" "Dublin"} 141}
              (sut/read-distances sample-input))))

(test/deftest part-1-sample
  (test/is (= 605 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 117 (sut/part-1))))

(test/deftest part-2-sample
  (test/is (= 982 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 909 (sut/part-2))))
