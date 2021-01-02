(ns advent-of-code-2017.day-16-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-16 :as sut]))

(test/deftest move
  (test/is (= "eabcd" (sut/move "abcde" "s1")))
  (test/is (= "eabdc" (sut/move "eabcd" "x3/4")))
  (test/is (= "baedc" (sut/move "eabdc" "pe/b"))))

(test/deftest part-1-sample
  (test/is (= "baedc" (sut/dance "abcde" ["s1" "x3/4" "pe/b"]))))

(test/deftest part-1
  (test/is (= "fnloekigdmpajchb" (sut/part-1))))

(test/deftest fast-dance
  (test/is (= "fnloekigdmpajchb" (sut/fast-dance "abcdefghijklmnop"))))

(test/deftest part-2
  (test/is (= "amkjepdhifolgncb" (sut/part-2))))
