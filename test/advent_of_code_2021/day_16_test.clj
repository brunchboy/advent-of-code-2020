(ns advent-of-code-2021.day-16-test
  "Unit tests for day 16."
  (:require [clojure.test :as test]
            [advent-of-code-2021.day-16 :as sut]))

(test/deftest part-1
  (test/is (= [{:version 6, :type 4, :literal 2021} 21]
              (sut/parse-packet (sut/hex->binary "D2FE28"))))
  (test/is (= [{:version  1,
                :type     6,
                :operator :less-than,
                :sub-packets
                [{:version 6, :type 4, :literal 10}
                 {:version 2, :type 4, :literal 20}]}
               49]
              (sut/parse-packet (sut/hex->binary "38006F45291200"))))
  (test/is (= 16 (sut/part-1 "8A004A801A8002F478")))
  (test/is (= 12 (sut/part-1 "620080001611562C8802118E34")))
  (test/is (= 23 (sut/part-1 "C0015000016115A2E0802F182340")))
  (test/is (= 31 (sut/part-1 "A0016C880162017C3686B18A3D4780")))
  (test/is (= 847 (sut/part-1))))

(test/deftest part-2
  (test/is (= 3 (sut/part-2 "C200B40A82")))
  (test/is (= 54 (sut/part-2 "04005AC33890")))
  (test/is (= 7 (sut/part-2 "880086C3E88112")))
  (test/is (= 9 (sut/part-2 "CE00C43D881120")))
  (test/is (= 1 (sut/part-2 "D8005AC2A8F0")))
  (test/is (= 0 (sut/part-2 "F600BC2D8F")))
  (test/is (= 0 (sut/part-2 "9C005AC2F8F0")))
  (test/is (= 1 (sut/part-2 "9C0141080250320F1802104A08")))
  (test/is (= 333794664059 (sut/part-2))))
