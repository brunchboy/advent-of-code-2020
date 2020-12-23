(ns advent-of-code-2020.day-23
  "Solutions for day 23.")


(def input
  "The cup ordering (puzzle input)."
  [5 9 8 1 6 2 7 3 4])

(defn find-destination
  "Given the label of the current cup and the cups remaining after three
  have been picked up, find the label of the destination cup where
  they should be placed, given the rules in the problem statement.
  Choose the first available cup whose label is less than the current
  cup, wrapping to the highest label if we hit zero."
  [current others]
  (let [others (set others)]
    (loop [result (if (> current 1) (dec current) 9)]
      (if (others result)
        result
        (recur (if (> result 1) (dec result) 9))))))

(defn move
  "Perform a single move in the game of cups. Remove three cups after
  the current (first) cup, find the destination cup after which they
  should be placed, and move them there. Leave the next current cup as
  the first cup in the list returned so we can always just work from
  the front (since they are arranged in a circle, this is valid)."
  [cups]
  (let [[current & others] cups
        picked (take 3 others)
        others (drop 3 others)
        [before after] (split-at (inc (.indexOf others (find-destination current others))) others)]
    (concat before picked after [current])))

(defn part-1
  "Solve part 1: Play 100 moves, then return the labels of the cups that
  follow cup 1 as a string."
  ([]
   (part-1 input))
  ([cups]
   (let [result (nth (iterate move cups) 100)
         [before after] (split-at (.indexOf result 1) result)]
     (apply str (concat (drop 1 after) before)))))
