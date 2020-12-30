(ns advent-of-code-2017.day-7-test
  (:require [clojure.test :as test]
            [advent-of-code-2017.day-7 :as sut]
            [clojure.string :as str]))

(def sample-input
  "The program descriptions making up the sample problem."
  (->> "pbga (66)
xhth (57)
ebii (61)
havc (66)
ktlj (57)
fwft (72) -> ktlj, cntj, xhth
qoyq (66)
padx (45) -> pbga, havc, qoyq
tknk (41) -> ugml, padx, fwft
jptl (61)
ugml (68) -> gyxo, ebii, jptl
gyxo (61)
cntj (57)"
       str/split-lines))

(test/deftest parse-program
  (test/is (= [["pbga" 66 #{}]
               ["xhth" 57 #{}]
               ["ebii" 61 #{}]
               ["havc" 66 #{}]
               ["ktlj" 57 #{}]
               ["fwft" 72 #{"cntj" "xhth" "ktlj"}]
               ["qoyq" 66 #{}]
               ["padx" 45 #{"qoyq" "havc" "pbga"}]
               ["tknk" 41 #{"ugml" "padx" "fwft"}]
               ["jptl" 61 #{}]
               ["ugml" 68 #{"ebii" "jptl" "gyxo"}]
               ["gyxo" 61 #{}]
               ["cntj" 57 #{}]]
              (map sut/parse-program sample-input))))

(test/deftest find-parents
  (test/is (= {"qoyq" #{"padx"},
               "ebii"               #{"ugml"},
               "havc"               #{"padx"},
               "ugml"               #{"tknk"},
               "cntj"               #{"fwft"},
               "jptl"               #{"ugml"},
               "xhth"               #{"fwft"},
               "pbga"               #{"padx"},
               "gyxo"               #{"ugml"},
               "ktlj"               #{"fwft"},
               "tknk"               #{},
               "padx"               #{"tknk"},
               "fwft"               #{"tknk"}}
              (sut/find-parents sample-input))))

(test/deftest part-1-sample
  (test/is (= [["tknk" #{}]] (sut/part-1 sample-input))))

(test/deftest part-1
  (test/is (= [["qibuqqg" #{}]] (sut/part-1 sut/input))))

(test/deftest program-map
  (test/is (= {"qoyq" [66 #{}],
               "ebii" [61 #{}],
               "havc" [66 #{}],
               "ugml" [68 #{"ebii" "jptl" "gyxo"}],
               "cntj" [57 #{}],
               "jptl" [61 #{}],
               "xhth" [57 #{}],
               "pbga" [66 #{}],
               "gyxo" [61 #{}],
               "ktlj" [57 #{}],
               "tknk" [41 #{"ugml" "padx" "fwft"}],
               "padx" [45 #{"qoyq" "havc" "pbga"}],
               "fwft" [72 #{"cntj" "xhth" "ktlj"}]}
              (sut/program-map sample-input))))

(test/deftest recursive-program-map
  (test/is (= {"qoyq"
               {:weight 66, :joint-weight 66, :children #{}, :child-weights #{}},
               "ebii"
               {:weight 61, :joint-weight 61, :children #{}, :child-weights #{}},
               "havc"
               {:weight 66, :joint-weight 66, :children #{}, :child-weights #{}},
               "ugml"
               {:weight        68,
                :joint-weight  251,
                :children      #{"ebii" "jptl" "gyxo"},
                :child-weights #{61}},
               "cntj"
               {:weight 57, :joint-weight 57, :children #{}, :child-weights #{}},
               "jptl"
               {:weight 61, :joint-weight 61, :children #{}, :child-weights #{}},
               "xhth"
               {:weight 57, :joint-weight 57, :children #{}, :child-weights #{}},
               "pbga"
               {:weight 66, :joint-weight 66, :children #{}, :child-weights #{}},
               "gyxo"
               {:weight 61, :joint-weight 61, :children #{}, :child-weights #{}},
               "ktlj"
               {:weight 57, :joint-weight 57, :children #{}, :child-weights #{}},
               "tknk"
               {:weight        41,
                :joint-weight  778,
                :children      #{"ugml" "padx" "fwft"},
                :child-weights #{251 243}},
               "padx"
               {:weight        45,
                :joint-weight  243,
                :children      #{"qoyq" "havc" "pbga"},
                :child-weights #{66}},
               "fwft"
               {:weight        72,
                :joint-weight  243,
                :children      #{"cntj" "xhth" "ktlj"},
                :child-weights #{57}}}
              (sut/recursive-program-map sample-input))))

(test/deftest joint-weight
  (let [programs (sut/program-map sample-input)]
    (test/is (= 61 (sut/joint-weight-memo programs "gyxo")))
    (test/is (= 251 (sut/joint-weight-memo programs "ugml")))
    (test/is (= 243 (sut/joint-weight-memo programs "padx")))
    (test/is (= 243 (sut/joint-weight-memo programs "fwft")))
    (test/is (= 778 (sut/joint-weight-memo programs "tknk")))))

(test/deftest part-2-sample
  (test/is (= 60 (sut/part-2 sample-input))))

(test/deftest part-2
  (test/is (= 1079 (sut/part-2 sut/input))))
