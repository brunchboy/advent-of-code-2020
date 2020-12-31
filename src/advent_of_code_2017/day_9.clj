(ns advent-of-code-2017.day-9
  (:require [clojure.java.io :as io]))

(defn read-garbage
  "Reads a garbage section, ignoring negated closing angle brackets. The
  `reader` is positioned just after the `<` which started this garbage
  section."
  [reader]
  (loop [total 0]
    (case (char (.read reader))
      \> total  ; We are done, return the character count.
      \! (do  ; Negate the next character by simply discarding it, counting neither.
           (.read reader)
           (recur total))
      (recur (inc total)))))  ; Some other character, count it.

(defn read-group
  "Reads a group and returns the total score of all groups encountered
  within it, plus the score assigned to this group itself. The `reader`
  is positioned just after the `{` which started this group."
  [score reader]
  (loop [total score]
    (case (char (.read reader))
      \} total  ; We reached the end of the group, return the score.
      \{ (recur (+ total (read-group (inc score) reader)))  ; Handle nested group
      \, (recur total)   ; Just move past the comma.
      \< (do  ; Skip a garbage section.
           (read-garbage reader)
           (recur total)))))

(defn part-1
  "Solve part 1 of the problem. Optionally you can pass a string to be
  read, for unit testing with the sample data."
  ([]
   (with-open [reader (-> "2017/day_9.txt" io/resource io/reader)]
     (assert (= \{ (char (.read reader))))
     (read-group 1 reader)))
  ([input]
   (with-open [reader (-> input char-array io/reader)]
     (assert (= \{ (char (.read reader))))
     (read-group 1 reader))))

(defn read-group-2
  "Reads a group and returns the count of garbage characters within it.
  The `reader` is positioned just after the `{` which started this
  group."
  [reader]
  (loop [total 0]
    (case (char (.read reader))
      \} total  ; We reached the end of the group, return the score.
      \{ (recur (+ total (read-group-2 reader)))  ; Handle nested group
      \, (recur total)   ; Just move past the comma.
      \< (recur (+ total (read-garbage reader))))))  ; Count a garbage section.

(defn part-2
  "Solve part 2 of the problem. Optionally you can pass a string to be
  read, for unit testing with the sample data."
  ([]
   (with-open [reader (-> "2017/day_9.txt" io/resource io/reader)]
     (assert (= \{ (char (.read reader))))
     (read-group-2 reader)))
  ([input]
   (with-open [reader (-> input char-array io/reader)]
     (assert (= \{ (char (.read reader))))
     (read-group-2 reader))))
