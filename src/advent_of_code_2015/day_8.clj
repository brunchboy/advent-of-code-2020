(ns advent-of-code-2015.day-8
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The list of quoted strings (the puzzle input)."
  (->> (io/resource "2015/day_8.txt")
       io/reader
       line-seq))

(defn dequote
  "Implements the quotation rules specified in the problem statement."
  [s]
  (loop [chars  (butlast (rest s))
         result []]
    (if (empty? chars)   ; We've reached the end of the string.
      (apply str result) ; Return our accumulated result as a string.
      (let [c     (first chars) ; Examine the next character.
            chars (rest chars)]
        (if (not= \\ c)  ; If it isn't a backslash,
          (recur chars (conj result c))  ; we just accumulate it in the result.
          (let [c     (first chars) ; Examine the character after the backslash.
                chars (rest chars)]
            (case c
              (\\ \")  ; If a backslash or quote, we just quoted it.
              (recur chars (conj result c))

              \x  ; If an x, we have two hex digits to convert to a character.
              (recur (drop 2 chars) (conj result (char (Integer/parseInt (apply str (take 2 chars)) 16)))))))))))

(defn part-1
  "Solve part 1"
  []
  (->> input
       (map (fn [line] (- (count line) (count (dequote line)))))
       (apply +)))

(defn part-2
  "Solve part 2"
  []
  (->> input
       (map (fn [line] (- (count (with-out-str (pr line))) (count line))))
       (apply +)))
