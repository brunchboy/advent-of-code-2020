(ns advent-of-code-2015.day-18-test
  "Unit tests for day 18."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2015.day-18 :as sut]))


(def sample-data
  (-> ".#.#.#
...##.
#....#
..#...
#.#..#
####.."
      str/split-lines
      vec))

(test/deftest part-1
  (test/is (= (-> "..##..
..##.#
...##.
......
#.....
#.##.."
                  str/split-lines
                  vec)
              (sut/step sample-data)))

  (let [fourth-state (-> "......
......
..##..
..##..
......
......"
                     str/split-lines
                     vec)]
    (test/is (= fourth-state (first (drop 4 (iterate sut/step sample-data)))))
    (test/is (= 4 (sut/count-lit fourth-state))))
  (test/is (= 768 (sut/part-1))))

(test/deftest part-2
  (test/is (= 781 (sut/part-2))))
