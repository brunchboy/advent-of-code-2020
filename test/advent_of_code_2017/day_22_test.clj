(ns advent-of-code-2017.day-22-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-22 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The trivial map for the sample problem."
  (-> "..#
#..
..."
      (str/split-lines)))

(test/deftest read-map
  (test/is (= {:virus #{[0 1] [2 0]}, :x 1, :y 1, :heading :up, :added 0}
              (sut/read-map sample-input))))

(test/deftest part-1-sample
  (test/is (= 5587 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 5447 (sut/part-1 sut/input))))

(test/deftest read-map-2
  (test/is (= {:virus   {[2 0] :infected, [0 1] :infected},
               :x       1,
               :y       1,
               :heading :up,
               :added   0}
              (sut/read-map-2 sample-input))))

(test/deftest part-2-sample
  (test/is (= 2511944 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 2511705 (sut/part-2 sut/input))))
