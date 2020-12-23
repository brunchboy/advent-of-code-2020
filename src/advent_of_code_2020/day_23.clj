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
  "Perform a single move in the game of crab cups. Remove three cups
  after the current (first) cup, find the destination cup after which
  they should be placed, and move them there. Leave the next current
  cup as the first cup in the list returned so we can always just work
  from the front (since they are arranged in a circle, this is
  valid)."
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

;; Because part 2 involves a vastly larger number of cups and moves,
;; we need much more efficient data structures too. These functions
;; below can solve part 1 as well, and I considered just replacing the
;; above versions with these new ones, but I decided to keep both
;; around for comparison.

(def part-2-input
  "The huge input for part 2."
  (concat input (range 10 1000001)))

(defn build-links
  "We represent our cups as a circular linked hash map. Each key in the
  map is a cup label, and each value is the label of the cup that is
  clockwise from it."
  [cups]
  (-> (apply hash-map (flatten (partition 2 1 cups)))
      (assoc (last cups) (first cups))))

(defn link-seq
  "In order to conveniently work with the clockwise series of cup
  labels, this function returns a lazy sequence of the labels you will
  see moving clockwise starting with the cup you supply. `links` is
  the circular linked hash map created by `build-links`."
  [links cup]
  (lazy-seq (cons cup (link-seq links (links cup)))))

(defn find-destination-2
  "Given the label of the current cup, the largest cup label, and the
  cups which are currently unavailable because they have been picked
  up, find the label of the destination cup after which they should be
  placed, given the rules in the problem statement. Choose the first
  available cup whose label is less than the current cup, wrapping to
  the highest label if we hit zero."
  [current max missing]
  (loop [result (if (> current 1) (dec current) max)]
    (if (some #(= result %) missing)
      (recur (if (> result 1) (dec result) max))
      result)))

(defn move-2
  "Perform a single move in the game of crab cups. Takes the circular
  linked hash map of cups and the label of the current cup. Unlinks
  three cups after the current cup (stitching in the cup which follows
  them), finds the destination cup after which they should be placed,
  and splices them there. Returns the updated linked hash map and the
  new current cup."
  [[cups current]]
  (let [removed     (take 3 (drop 1 (link-seq cups current)))
        next        (cups (last removed))
        destination (find-destination-2 current (count cups) removed)
        after       (cups destination)]
    [(-> cups
          (assoc current next)
          (assoc destination (first removed))
          (assoc (last removed) after))
     next]))

(defn part-2
  "Solve part 2: Build the circular linked hash map, play ten million
  moves, then return the product of two cups that follow cup 1."
  ([]
   (part-2 part-2-input))
  ([cups]
   (let [result (nth (iterate move-2 [(build-links cups) (first cups)]) 10000000)]
     (apply * (take 2 (drop 1 (link-seq (first result) 1)))))))
