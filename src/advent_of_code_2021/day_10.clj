(ns advent-of-code-2021.day-10
  "Solutions for day 10."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def closers
  "For each opening delimiter, identifies the expected closing
  delimiter."
  {\{ \}
   \( \)
   \[ \]
   \< \>})

(def mismatch-scores
  "The number of points assigned to each incorrect closing delimiter in
  the problem statement."
  {\) 3
   \] 57
   \} 1197
   \> 25137})

(def input
  "The puzzle input."
  (-> (io/resource "2021/day_10.txt")
      slurp
      str/split-lines))

(defn improper-closing-delimiter
  "If a puzzle line contains a closing delimiter that does not match the
  opening delimiter it is associated with, returns the first such
  incorrect delimiter. If the line ends prematurely, returns the list
  of expected closing delimiters that are missing, in order. (The
  first version of this simply returned `nil` for premature lines, as
  needed for part 1, but it was happily easy to add support for part
  2."
  [line]
  (loop [c       (first line)
         line    (rest line)
         openers ()]
    (if c
      (cond (#{\{ \( \[ \<} c)
            (recur (first line)
                   (rest line)
                   (conj openers c))

            (= c (closers (first openers)))
            (recur (first line)
                   (rest line)
                   (rest openers))

            :else  ; A mis-match, report it.
            c)
      (map closers openers))))

(defn score
  "Calculate the score for a mismatched line. If there is not a
  mismatched closing delimiter, this will be 0."
  [bad-delimiter]
  (get mismatch-scores bad-delimiter 0))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (apply + (map (comp score improper-closing-delimiter) data))))

(def missing-scores
  "Calculate the score for an individual missing delimiter on an
  incomplete line, as specified in the problem statement."
  {\) 1
   \] 2
   \} 3
   \> 4})

(defn score-2
  "Calculate the score for an incomplete line, as specified in the
  problem statement."
  [bad-delimiter]
  (if (seq? bad-delimiter)
    (loop [score 0
           delim (first bad-delimiter)
           left  (rest bad-delimiter)]
      (if delim
        (recur (+ (* score 5) (get missing-scores delim))
               (first left)
               (rest left))
        score))
    0))  ; It was not a list, so it was not an incomplete line.

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (let [scores (->> (map (comp score-2 improper-closing-delimiter) data)
                     (filter pos?)
                     sort
                     )]
     (nth scores (quot (count scores) 2)))))
