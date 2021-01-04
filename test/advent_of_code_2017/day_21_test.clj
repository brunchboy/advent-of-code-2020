(ns advent-of-code-2017.day-21-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-21 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The enhancement rule book from the sample problem."
  (-> "../.# => ##./#../...
.#./..#/### => #..#/..../..../#..#"
      str/split-lines))

(test/deftest build-rulebook
  (test/is (= {[".." ".#"]         ["##." "#.." "..."],
               [".#." "..#" "###"] ["#..#" "...." "...." "#..#"]}
              (sut/build-rulebook sample-input))))

(test/deftest slice-image
  (test/is (= [[[".#."
                 "..#"
                 "###"]]]
              (sut/slice-image sut/starting-image)))
  (test/is (= [[["12"
                 "56"]
                ["34"
                 "78"]]
               [["9a"
                 "de"]
                ["bc"
                 "f0"]]]
              (sut/slice-image ["1234"
                                "5678"
                                "9abc"
                                "def0"])))
  (test/is (= [[["123" "abc" "jkl"] ["456" "def" "mno"] ["789" "ghi" "pqr"]]
               [["stu" "ABC" "JKL"] ["vwx" "DEF" "MNO"] ["yz0" "GHI" "PQR"]]
               [["STU" "!@#" "-_="] ["VWX" "$%^" "+[]"] ["YZ0" "&*(" "{}|"]]]
              (sut/slice-image ["123456789"
                                "abcdefghi"
                                "jklmnopqr"
                                "stuvwxyz0"
                                "ABCDEFGHI"
                                "JKLMNOPQR"
                                "STUVWXYZ0"
                                "!@#$%^&*("
                                "-_=+[]{}|"]))))

(defn test-reassembly
  "Makes sure that slicing and merging an image results in the same image."
  [image]
  (test/is (= image (sut/merge-slices (sut/slice-image image)))))

(test/deftest merge-slices
  (test-reassembly sut/starting-image)
  (test-reassembly ["1234"
                    "5678"
                    "9abc"
                    "def0"])
  (test-reassembly ["123456789"
                    "abcdefghi"
                    "jklmnopqr"
                    "stuvwxyz0"
                    "ABCDEFGHI"
                    "JKLMNOPQR"
                    "STUVWXYZ0"
                    "!@#$%^&*("
                    "-_=+[]{}|"]))

(test/deftest expand-tile
  (let [rule-book (sut/build-rulebook sample-input)]
    (test/is (= ["#..#" "...." "...." "#..#"]
                (sut/expand-tile rule-book sut/starting-image)))))

(test/deftest enhance
  (let [rule-book (sut/build-rulebook sample-input)]
    (test/is (= ["#..#" "...." "...." "#..#"]
                (sut/enhance rule-book sut/starting-image)))
    (test/is (= [[".#." "..#" "###"]
                 ["#..#" "...." "...." "#..#"]
                 ["##.##." "#..#.." "......" "##.##." "#..#.." "......"]]
                (take 3 (sut/build-enhancer rule-book sut/starting-image))))))

(test/deftest part-1-sample
  (test/is (= 12 (sut/part-1 sample-input 2))))

(test/deftest part-1
  (test/is (= 144 (sut/part-1 sut/input 5))))

(test/deftest part-2
  (test/is (= 2169301 (sut/part-1 sut/input 18))))
