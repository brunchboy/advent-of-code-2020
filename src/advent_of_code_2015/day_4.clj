(ns advent-of-code-2015.day-4
  (:import (java.security MessageDigest)
           (java.math BigInteger)))

(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

(defn part-1
  "Brute-force search for the first positive integer which when appended
  to the input yields an MD5 hash with five leading zeroes."
  [input]
  (first (filter (fn [i] (= (subs (md5 (str input i)) 0 5) "00000")) (map inc (range)))))

(defn part-2
  "Brute-force search for the first positive integer which when appended
  to the input yields an MD5 hash with six leading zeroes."
  [input]
  (first (filter (fn [i] (= (subs (md5 (str input i)) 0 6) "000000")) (map inc (range)))))
