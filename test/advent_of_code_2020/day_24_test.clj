(ns advent-of-code-2020.day-24-test
  "Unit tests for day 24."
  (:require [clojure.test :as test]
            [advent-of-code-2020.day-24 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The tile paths from the sample problem."
  (str/split-lines "sesenwnenenewseeswwswswwnenewsewsw
neeenesenwnwwswnenewnwwsewnenwseswesw
seswneswswsenwwnwse
nwnwneseeswswnenewneswwnewseswneseene
swweswneswnenwsewnwneneseenw
eesenwseswswnenwswnwnwsewwnwsene
sewnenenenesenwsewnenwwwse
wenwwweseeeweswwwnwwe
wsweesenenewnwwnwsenewsenwwsesesenwne
neeswseenwwswnwswswnw
nenwswwsewswnenenewsenwsenwnesesenew
enewnwewneswsewnwswenweswnenwsenwsw
sweneswneswneneenwnewenewwneswswnese
swwesenesewenwneswnwwneseswwne
enesenwswwswneneswsenwnewswseenwsese
wnwnesenesenenwwnenwsewesewsesesew
nenewswnwewswnenesenwnesewesw
eneswnwswnwsenenwnwnwwseeswneewsenese
neswnwewnwnwseenwseesewsenwsweewe
wseweeenwnesenwwwswnew"))

(test/deftest part-1-sample
  (test/is (= 10 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 549 (sut/part-1))))

(test/deftest part-2-sample
  (test/is (= 2208 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 4147 (sut/part-2))))
