(ns advent-of-code-2017.day-9-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-9 :as sut]))

(test/deftest basic-groups
  (test/is (= 1 (sut/part-1 "{}")))
  (test/is (= 6 (sut/part-1 "{{{}}}")))
  (test/is (= 5 (sut/part-1 "{{},{}}")))
  (test/is (= 16 (sut/part-1 "{{{},{},{{}}}}"))))

(test/deftest with-garbage
  (test/is (= 1 (sut/part-1 "{<a>,<a>,<a>,<a>}")))
  (test/is (= 9 (sut/part-1 "{{<ab>},{<ab>},{<ab>},{<ab>}}")))
  (test/is (= 9 (sut/part-1 "{{<!!>},{<!!>},{<!!>},{<!!>}}")))
  (test/is (= 3 (sut/part-1 "{{<a!>},{<a!>},{<a!>},{<ab>}}"))))

(test/deftest part-1
  (test/is (= 12505 (sut/part-1))))

(test/deftest counted-garbage
  (test/is (= 0 (sut/part-2 "{<>}")))
  (test/is (= 17 (sut/part-2 "{<random characters>}")))
  (test/is (= 3 (sut/part-2 "{<<<<>}")))
  (test/is (= 2 (sut/part-2 "{<{!>}>}")))
  (test/is (= 0 (sut/part-2 "{<!!>}")))
  (test/is (= 0 (sut/part-2 "{<!!!>>}")))
  (test/is (= 10 (sut/part-2 "{<{o\"i!a,<{i<a>}"))))

(test/deftest part-2
  (test/is (= 6671 (sut/part-2))))
