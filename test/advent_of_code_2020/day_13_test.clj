(ns advent-of-code-2020.day-13-test
  "Unit tests for day 13."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2020.day-13 :as sut]))

(def sample-bus-ids
  "The bus IDs on which most example results were based."
  (str/split "7,13,x,x,59,x,31,19" #","))

(test/deftest best-bus
  (test/is (= [59 5] (sut/best-bus 939 (->> sample-bus-ids
                                            (remove #(= "x" %))
                                            (map #(Long/parseLong %)))))))


(test/deftest part-1-sample
  (test/is (= 295 (sut/part-1 939 (->> sample-bus-ids
                                        (remove #(= "x" %))
                                        (map #(Long/parseLong %)))))))

(test/deftest part-1
  (test/is (= 104 (sut/part-1))))

(test/deftest part-2-samples
  (test/is (= 1068781 (sut/part-2 sample-bus-ids)))
  (test/is (= 3417 (sut/part-2 (str/split "17,x,13,19" #","))))
  (test/is (= 754018 (sut/part-2 (str/split "67,7,59,61", #","))))
  (test/is (= 779210 (sut/part-2 (str/split "67,x,7,59,61", #","))))
  (test/is (= 1261476 (sut/part-2 (str/split "67,7,x,59,61", #","))))
  (test/is (= 1202161486 (sut/part-2 (str/split "1789,37,47,1889", #",")))))

(test/deftest part-2
  (test/is (= 842186186521918 (sut/part-2))))
