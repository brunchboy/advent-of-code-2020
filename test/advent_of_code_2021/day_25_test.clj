(ns advent-of-code-2021.day-25-test
  "Unit tests for day 25."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-25 :as sut]
            [clojure.string :as str]))

(def sample-1
  (->> "...>...
.......
......>
v.....>
......>
.......
..vvv..
"
      (str/split-lines)
      (mapv vec)))

(def sample-1-stepped
  (->> "..vv>..
.......
>......
v.....>
>......
.......
....v.."
                   (str/split-lines)
                   (mapv vec)))

(def sample-1-stepped-4
  (->> ">......
..v....
..>.v..
.>.v...
...>...
.......
v......"
       (str/split-lines)
       (mapv vec)))

(def sample-2
  (->> "v...>>.vv>
.vv>>.vv..
>>.>v>...v
>>v>>.>.v.
v>v.vv.v..
>.>>..v...
.vv..>.>v.
v.v..>>v.v
....v..v.>"
       (str/split-lines)
       (mapv vec)))

(def sample-2-converged
  (->> "..>>v>vv..
..v.>>vv..
..>>v>>vv.
..>>>>>vv.
v......>vv
v>v....>>v
vvv.....>>
>vv......>
.>v.vv.v.."
       (str/split-lines)
       (mapv vec)))

(test/deftest part-1
  (test/is (= sample-1-stepped (sut/step sample-1)))
  (test/is (= sample-1-stepped-4 (first (drop 4 (iterate sut/step sample-1)))))
  (test/is (= sample-2-converged (first (drop 58 (iterate sut/step sample-2)))))
  (test/is (= sample-2-converged (first (drop 59 (iterate sut/step sample-2)))))
  (test/is (= 58 (sut/steps-to-convergence sample-2)))
  (test/is (= 337 (sut/steps-to-convergence sut/input))))
