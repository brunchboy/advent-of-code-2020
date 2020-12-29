(ns advent-of-code-2017.day-4-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-4 :as sut]))

(test/deftest no-repetitions
  (test/is (sut/no-repetitions? "aa bb cc dd ee"))
  (test/is (not (sut/no-repetitions? "aa bb cc dd aa")))
  (test/is (sut/no-repetitions? "aa bb cc dd aaa")))

(test/deftest part-1
  (test/is (= 337 (sut/part-1 sut/input))))

(test/deftest no-anagrams
  (test/is (sut/no-anagrams? "abcde fghij"))
  (test/is (not (sut/no-anagrams? "abcde xyz ecdab")))
  (test/is (sut/no-anagrams? "a ab abc abd abf abj"))
  (test/is (sut/no-anagrams? "iiii oiii ooii oooi oooo"))
  (test/is (not (sut/no-anagrams? "oiii ioii iioi iiio"))))

(test/deftest part-2
  (test/is (= 231 (sut/part-2 sut/input))))
