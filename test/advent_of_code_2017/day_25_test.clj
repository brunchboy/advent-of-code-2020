(ns advent-of-code-2017.day-25-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-25 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The Turing machine blueprint for the sample problem."
  (str/split-lines "Begin in state A.
Perform a diagnostic checksum after 6 steps.

In state A:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state B.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state B.

In state B:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state A."))

(test/deftest read-blueprint
  (test/is (= {:state    "A"
               :pos      0
               :tape     #{}
               :states   {"A" {0 [1 :right "B"], 1 [0 :left "B"]}
                          "B" {0 [1 :left "A"], 1 [1 :right "A"]}}
               :step     0
               :diagnose 6}
              (sut/read-blueprint sample-input))))

(test/deftest part-1-sample
  (test/is (= 3 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 4387 (sut/part-1 sut/input))))
