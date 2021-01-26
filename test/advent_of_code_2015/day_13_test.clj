(ns advent-of-code-2015.day-13-test
  (:require [clojure.string :as str]
            [clojure.test :as test]
            [advent-of-code-2015.day-13 :as sut]))

(def sample-input
  "The sample list of happiness changes."
  (-> "Alice would gain 54 happiness units by sitting next to Bob.
Alice would lose 79 happiness units by sitting next to Carol.
Alice would lose 2 happiness units by sitting next to David.
Bob would gain 83 happiness units by sitting next to Alice.
Bob would lose 7 happiness units by sitting next to Carol.
Bob would lose 63 happiness units by sitting next to David.
Carol would lose 62 happiness units by sitting next to Alice.
Carol would gain 60 happiness units by sitting next to Bob.
Carol would gain 55 happiness units by sitting next to David.
David would gain 46 happiness units by sitting next to Alice.
David would lose 7 happiness units by sitting next to Bob.
David would gain 41 happiness units by sitting next to Carol."
      str/split-lines))

(test/deftest read-happiness
  (test/is (= {["Alice" "Carol"] -79,
               ["David" "Carol"] 41,
               ["Alice" "Bob"]   54,
               ["David" "Bob"]   -7,
               ["Carol" "Alice"] -62,
               ["Bob" "Alice"]   83,
               ["Bob" "Carol"]   -7,
               ["David" "Alice"] 46,
               ["Bob" "David"]   -63,
               ["Carol" "David"] 55,
               ["Alice" "David"] -2,
               ["Carol" "Bob"]   60}
              (sut/read-happiness sample-input))))

(test/deftest all-people
  (test/is (= #{"Alice" "Carol" "Bob" "David"} (set (map first (keys (sut/read-happiness sample-input)))))))

(test/deftest part-1-sample
  (test/is (= 330 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 709 (sut/part-1))))

(test/deftest part-2
  (test/is (= 668 (sut/part-2))))
