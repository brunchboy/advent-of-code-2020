(ns advent-of-code-2020.day-8-test
  "Unit tests for day 8."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-8 :as sut]
            [clojure.string :as str]))

(def test-input
  "The sample program from the problem statement."
  (str/split-lines "nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6"))

(test/deftest part-1
  (test/is (= 5 (sut/part-1 test-input)))
  (test/is (= 1420 (sut/part-1))))

(test/deftest part-2
  (test/is (= 1245 (sut/part-2))))
