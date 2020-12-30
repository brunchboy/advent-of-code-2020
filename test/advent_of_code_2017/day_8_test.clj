(ns advent-of-code-2017.day-8-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-8 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The sequence of instructions that make up the sample problem."
  (str/split-lines "b inc 5 if a > 1
a inc 1 if b < 5
c dec -10 if a >= 1
c inc -20 if c == 10"))

(test/deftest parse-lines
  (test/is (= [{:destination "b", :delta 5}
               {:destination "a", :delta 1}
               {:destination "c", :delta 10}
               {:destination "c", :delta -20}]
              (->> sample-input
                   (map sut/parse-instruction)
                   (map #(dissoc % :test)))))
  (let [parsed (sut/parse-instruction "c dec -10 if a >= 1")
        test   (:test parsed)]
    (test/is (test {"a" 1}))
    (test/is (test {"a" 3}))
    (test/is (not (test {"a" 0})))
    (test/is (not (test {"z" 7})))))

(test/deftest execute
  (test/is (= {} (sut/execute {} (sut/parse-instruction "b inc 5 if a > 1"))))
  (test/is (= {"a" 1} (sut/execute {} (sut/parse-instruction "a inc 1 if b < 5")))))

(test/deftest run
  (test/is (= {"a" 1, "c" -10} (sut/run sample-input))))

(test/deftest part-1-sample
  (test/is (= 1 (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= 5143 (sut/part-1 sut/input))))

(test/deftest part-2-sample
  (test/is (= 10 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 6209 (sut/part-2 sut/input))))
