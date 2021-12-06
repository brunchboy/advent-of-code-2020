(ns advent-of-code-2021.day-6-test
  "Unit tests for day 6."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-6 :as sut]))

(test/deftest part-1
  (test/is (= 26 (count (last (take 19 (iterate sut/step [3 4 3 1 2]))))))
  (test/is (= 5934 (count (last (take 81 (iterate sut/step [3 4 3 1 2]))))))
  (test/is (= 353274 (sut/part-1))))

(test/deftest part-2
  (test/is (= 26 (apply + (vals (last (take 19 (iterate sut/step-2 (frequencies [3 4 3 1 2]))))))))
  (test/is (= 5934 (apply + (vals (last (take 81 (iterate sut/step-2 (frequencies [3 4 3 1 2]))))))))
  (test/is (= 26984457539 (apply + (vals (last (take 257 (iterate sut/step-2 (frequencies [3 4 3 1 2]))))))))
  (test/is (= 1609314870967 (sut/part-2))))
