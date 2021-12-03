(ns advent-of-code-2015.day-15-test
  "Unit tests for day 15."
  (:require [clojure.test :as test]
            [advent-of-code-2015.day-15 :as sut]))

(def sample-data
  "The sample ingredients from the problem statement."
  {"Butterscotch" [-1 -2 6 3 8]
   "Cinnamon"     [2 3 -2 -1 3]})

(defn solve-test-data
  "Start with a simple loop to validate concept of brute force search
  through recipe space."
  []
  (let [all-cookies (for [butterscotch (range 101)]
                      (let [chosen {"Butterscotch" butterscotch
                                    "Cinnamon" (- 100 butterscotch)}]
                        [(sut/score sample-data chosen) chosen]))]
    (reduce (fn [[score-1 chosen-1] [score-2 chosen-2]]
              (if (> score-1 score-2)
                [score-1 chosen-1]
                [score-2 chosen-2]))
            all-cookies)))

(defn solve-test-data-with-calories
  "Add the restriction that we only consider recipes which add up to 500
  calories."
  []
  (let [all-cookies (for [butterscotch (range 101)]
                      (let [chosen {"Butterscotch" butterscotch
                                    "Cinnamon" (- 100 butterscotch)}]
                        [(sut/score sample-data chosen) chosen]))]
    (reduce (fn [[score-1 chosen-1] [score-2 chosen-2]]
              (if (> score-1 score-2)
                [score-1 chosen-1]
                [score-2 chosen-2]))
            (filter #(= 500 (sut/calculate-property sample-data (second %) 4)) all-cookies))))

(test/deftest with-sample-data
  (test/is (= [62842880 {"Butterscotch" 44, "Cinnamon" 56}] (solve-test-data))))

(test/deftest part-1
  (test/is (= [13882464 {"Sprinkles" 28, "PeanutButter" 35, "Frosting" 18, "Sugar" 19}] (sut/part-1))))

(test/deftest with-sample-data-and-calories
  (test/is (= [57600000 {"Butterscotch" 40, "Cinnamon" 60}] (solve-test-data-with-calories))))

(test/deftest part-2
  (test/is (= [11171160 {"Sprinkles" 27, "PeanutButter" 27, "Frosting" 15, "Sugar" 31}] (sut/part-2))))
