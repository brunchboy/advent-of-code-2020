(ns advent-of-code-2021.day-10-test
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2021.day-10 :as sut]))

(def sample-input
  "The input for the sample problem."
  (-> "[({(<(())[]>[[{[]{<()<>>
[(()[<>])]({[<{<<[]>>(
{([(<{}[<>[]}>{[]{[(<()>
(((({<>}<{<{<>}{[]{[]{}
[[<[([]))<([[{}[[()]]]
[{[{({}]{}}([{[{{{}}([]
{<[[]]>}<{[{[{[]{()[[[]
[<(<(<(<{}))><([]([]()
<{([([[(<>()){}]>(<<{{
<{([{{}}[<[[[<>{}]]]>[]]"
      str/split-lines))

(test/deftest part-1
  (test/is (= 26397 (sut/part-1 sample-input)))
  (test/is (= 294195 (sut/part-1))))

(test/deftest part-2
  (test/is (= 288957 (sut/part-2 sample-input)))
  (test/is (= 3490802734 (sut/part-2))))
