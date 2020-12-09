(ns advent-of-code-2020.day-9-test
  "Unit tests for day 9."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-9 :as sut]
            [clojure.string :as str]))

(def test-input
  "The sample program from the problem statement."
  (->> "35
20
15
25
47
40
62
55
65
95
102
117
150
182
127
219
299
277
309
576"
       str/split-lines
       (map #(Long/parseLong %))))

(test/deftest first-invalid
  (test/is (= 127 (sut/first-invalid 5 test-input))))

(test/deftest part-1
  (test/is (= 1212510616 (sut/part-1))))

(test/deftest contiguous-match
  (test/is (= [15 25 47 40] (sut/contiguous-match 127 test-input))))

(test/deftest match-extraction
  (let [candidates (sut/contiguous-match 127 test-input)]
    (test/is (= 62 (+ (apply min candidates) (apply max candidates))))))

(test/deftest part-2
  (test/is (= 171265123 (sut/part-2))))
