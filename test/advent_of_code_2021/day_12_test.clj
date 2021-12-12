(ns advent-of-code-2021.day-12-test
  "Unit tests for day 11."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-12 :as sut]))

(def sample-input
  "The first, simple, cave system."
  (sut/read-edges "start-A
start-b
A-c
A-b
b-d
A-end
b-end"))

(def sample-input-2
  "The slightly more complex example cave system."
  (sut/read-edges "dc-end
HN-start
start-kj
dc-start
dc-HN
LN-dc
HN-end
kj-sa
kj-HN
kj-dc"))

(def sample-input-3
  "The most complex example cave system."
  (sut/read-edges "fs-end
he-DX
fs-he
start-DX
pj-DX
end-zg
zg-sl
zg-pj
pj-he
RW-he
fs-DX
pj-RW
zg-RW
start-pj
he-WI
zg-he
pj-fs
start-RW"))

(test/deftest part-1
  (test/is (= ["d" "A" "end"] (sut/eligible-next-caves sample-input ["start" "b"])))
  (test/is (= [["start" "b" "A" "end"]
               ["start" "b" "A" "c" "A" "end"]
               ["start" "b" "end"]
               ["start" "A" "b" "A" "end"]
               ["start" "A" "b" "A" "c" "A" "end"]
               ["start" "A" "b" "end"]
               ["start" "A" "end"]
               ["start" "A" "c" "A" "b" "A" "end"]
               ["start" "A" "c" "A" "b" "end"]
               ["start" "A" "c" "A" "end"]] (sut/part-1 sample-input)))
  (test/is (= 19 (count (sut/part-1 sample-input-2))))
  (test/is (= 226 (count (sut/part-1 sample-input-3))))
  (test/is (= 3497 (count (sut/part-1 sut/input)))))

(test/deftest part-2
  (test/is (= 36 (count (sut/part-2 sample-input))))
  (test/is (= 103 (count (sut/part-2 sample-input-2))))
  (test/is (= 3509 (count (sut/part-2 sample-input-3))))
  (test/is (= 93686 (count (sut/part-2)))))
