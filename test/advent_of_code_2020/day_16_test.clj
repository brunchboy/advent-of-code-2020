(ns advent-of-code-2020.day-16-test
  "Unit tests for day 16."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2020.day-16 :as sut]))

(def sample-notes
  "The ticket notes for the sample problem."
  (str/split-lines "class: 1-3 or 5-7
row: 6-11 or 33-44
seat: 13-40 or 45-50

your ticket:
7,1,14

nearby tickets:
7,3,47
40,4,50
55,2,20
38,6,12"))

(test/deftest parse-notes
  (test/is (= {:rules
               {"class" '((1 3) (5 7)),
                "row"   '((6 11) (33 44)),
                "seat"  '((13 40) (45 50))},
               :mine   '[7 1 14],
               :nearby '([7 3 47] [40 4 50] [55 2 20] [38 6 12])}
              (sut/parse-notes sample-notes))))

(test/deftest part-1-sample
  (test/is (= 71 (sut/part-1 sample-notes))))

(test/deftest part-1
  (test/is (= 20048 (sut/part-1))))

(test/deftest remove-invalid-tickets-sample
  (let [{:keys [rules nearby]} (sut/parse-notes sample-notes)]
    (test/is (= '([7 3 47])
                (sut/remove-invalid-tickets rules nearby)))))

(def sample-notes-2
  "The ticket notes for the Part 2 sample problem."
  (str/split-lines "class: 0-1 or 4-19
row: 0-5 or 8-19
seat: 0-13 or 16-19

your ticket:
11,12,13

nearby tickets:
3,9,18
15,1,5
5,14,9"))

(test/deftest part-2
  (test/is (= 4810284647569 (sut/part-2))))
