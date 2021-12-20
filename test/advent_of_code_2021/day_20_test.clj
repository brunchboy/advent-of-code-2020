(ns advent-of-code-2021.day-20-test
  "Unit tests for day 20."
  (:require [clojure.test :as test]
            [clojure.string :as str]
            [advent-of-code-2021.day-20 :as sut]))

(def sample-enhancement "..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#")

(def sample-input
  (->> "#..#.
#....
##..#
..#..
..###"
       str/split-lines
       sut/read-image))

(test/deftest part-1
  (test/is (= (sut/enhancement-index sample-input [2 2]) 34))
  (test/is (= 35 (count (sut/step-twice sample-enhancement sample-input))))
  (test/is (= 5359 (count (sut/step-twice sut/enhancement sut/image)))))

(test/deftest part-2
  (test/is (= 3351 (count (first (drop 25 (iterate (partial sut/step-twice sample-enhancement) sample-input))))))
  (test/is (= 12333 (count (first (drop 25 (iterate (partial sut/step-twice sut/enhancement) sut/image)))))))
