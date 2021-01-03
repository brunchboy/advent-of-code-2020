(ns advent-of-code-2017.day-20-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-20 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The trivial pair of sample particles."
  (-> "p=<3,0,0>, v=<2,0,0>, a=<-1,0,0>
p=<4,0,0>, v=<0,0,0>, a=<-2,0,0>"
      str/split-lines))

(test/deftest read-particle
  (test/is (= [{:p [3 0 0], :v [2 0 0], :a [-1 0 0], :index 0}
               {:p [4 0 0], :v [0 0 0], :a [-2 0 0], :index 1}]
              (map-indexed sut/read-particle sample-input))))

(test/deftest magnitude
  (let [particles (map-indexed sut/read-particle sample-input)]
    (test/is (= [1.0 2.0] (map (partial sut/magnitude :a) particles)))
    (test/is (= [2.0 0.0] (map (partial sut/magnitude :v) particles)))))

(test/deftest part-1
  (test/is (= 144 (sut/part-1 sut/input))))

(test/deftest step
  (test/is (= [{:p [3 0 0], :v [-1 0 0], :a [-1 0 0], :index 0}
               {:p [-8 0 0], :v [-6 0 0], :a [-2 0 0], :index 1}]
              (nth (sut/system-sequence sample-input) 3))))

(test/deftest part-2
  (test/is (= 477 (sut/part-2 sut/input))))
