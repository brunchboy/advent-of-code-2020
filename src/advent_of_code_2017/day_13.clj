(ns advent-of-code-2017.day-13
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The firewall configuration description (puzzle input)."
  (->> "2017/day_13.txt"
       io/resource
       io/reader
       line-seq))

(defn read-config-line
  "Parses a line of firewall configuration information, returning a
  tuple of the depth and range of an active layer."
  [line]
  (->> (str/split line #": ")
       (map #(Long/parseLong %))
       vec))

(defn severity
  "Calculate the severity of a path through the specified firewall
  configuration lines."
  [lines]
  (reduce (fn [acc [depth range]]
            (if (or (< range 2)
                    (zero? (mod depth (* 2 (dec range)))))
              (+ acc (* depth range))
              acc))
          0
          (map read-config-line lines)))

(defn part-2
  "An ugly brute force solution to find an interval that makes it past
  all the layers. There is probably a way to use the Chinese Remainder
  Theorem here, but I can't be bothered if this runs fast enough."
  [lines]
  (let [layers (map read-config-line lines)]
    (loop [delay 0]
      (if (empty? (filter (fn [[depth range]]
                            (zero? (mod (+ depth delay) (* 2 (dec range)))))
                          layers))
        delay
        (recur (inc delay))))))
