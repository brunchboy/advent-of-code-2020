(ns advent-of-code-2017.day-18-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-18 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The sample program."
  (-> "set a 1
add a 2
mul a a
mod a 5
snd a
set a 0
rcv a
jgz a -1
set a 1
jgz a -2"
      (str/split-lines)))

(test/deftest run
  (test/is (= {:pc 3 "a" 9} (dissoc (nth (sut/run sample-input) 3) :code))))

(test/deftest part-1-sample
  (test/is (= 4 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 9423 (sut/part-1 sut/input))))

(def sample-input-2
  "The concurrent sample program."
  (-> "snd 1
snd 2
snd p
rcv a
rcv b
rcv c
rcv d"
      (str/split-lines)))

(test/deftest part-2-sample
  (test/is (= 3 (sut/part-2 sample-input-2))))

(test/deftest part-2
  (test/is (= 7620 (sut/part-2 sut/input))))
