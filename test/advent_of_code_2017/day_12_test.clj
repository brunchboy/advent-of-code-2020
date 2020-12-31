(ns advent-of-code-2017.day-12-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-12 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The connectivity list for the sample problem."
  (->> "0 <-> 2
1 <-> 1
2 <-> 0, 3, 4
3 <-> 2, 4
4 <-> 2, 3, 6
5 <-> 6
6 <-> 4, 5"
       (str/split-lines)))

(test/deftest parse-line
  (test/is (= [0 #{2}] (sut/parse-line "0 <-> 2")))
  (test/is (= [2 #{0 3 4}] (sut/parse-line "2 <-> 0, 3, 4"))))

(test/deftest add-connectivity
  (test/is (= {0 #{0 2}, 2 #{0 2}}
              (sut/add-connectivity {} #{0 2})))
  (test/is (= {0 #{0 4 3 2}, 2 #{0 4 3 2}, 4 #{0 4 3 2}, 3 #{0 4 3 2}}
              (sut/add-connectivity {0 #{0 2}, 2 #{0 2}}
                                    #{2 0 3 4})))
  (test/is (= {0 #{0 4 6 3 2 5},
               2 #{0 4 6 3 2 5},
               4 #{0 4 6 3 2 5},
               3 #{0 4 6 3 2 5},
               6 #{0 4 6 3 2 5},
               5 #{0 4 6 3 2 5}}
              (sut/add-connectivity {0 #{0 4 3 2}, 2 #{0 4 3 2}, 4 #{0 4 3 2}, 3 #{0 4 3 2}}
                                    #{6 4 5}))))

(test/deftest part-1-sample
  (test/is (= 6 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 169 (sut/part-1))))

(test/deftest part-2-sample
  (test/is (= 2 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 179 (sut/part-2))))
