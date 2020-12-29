(ns advent-of-code-2017.day-1-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-1 :as sut]))

(test/deftest part-1-samples
  (test/is (= 3 (sut/part-1 "1122")))
  (test/is (= 4 (sut/part-1 "1111")))
  (test/is (= 0 (sut/part-1 "1234")))
  (test/is (= 9 (sut/part-1 "91212129"))))

(test/deftest part-1
  (test/is (= 995 (sut/part-1 sut/input))))

(test/deftest part-2-samples
  (test/is (= 6 (sut/part-2 "1212")))
  (test/is (= 0 (sut/part-2 "1221")))
  (test/is (= 4 (sut/part-2 "123425")))
  (test/is (= 12 (sut/part-2 "123123")))
  (test/is (= 4 (sut/part-2 "12131415"))))

(test/deftest part-2
  (test/is (= 1130 (sut/part-2 sut/input))))
