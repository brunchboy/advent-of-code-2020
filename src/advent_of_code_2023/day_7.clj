(ns advent-of-code-2023.day-7
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def sample-input
  "The sample puzzle input."
  "32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483")

(def input
  "The real puzzle input."
  (->> "2023/day_7.txt"
       io/resource
       slurp))

(defn type-score
  "Calculates the relative value of the type of a hand."
  [hand]
  (let [groups (->> hand
                    frequencies
                    vals
                    sort
                    reverse)]
    (case (first groups)
      5 6
      4 5
      3 (case (second groups)
          2 4
          3)
      2 (case (second groups)
          2 2
          1)
      0)))

(def card-score
  "A map of card characters to their relative score values."
  {\2 2
   \3 3
   \4 4
   \5 5
   \6 6
   \7 7
   \8 8
   \9 9
   \T 10
   \J 11
   \Q 12
   \K 13
   \A 14})

(defn compare-hands
  "A comparator that determines the relative ranks of a pair of hands."
  [hand-1 hand-2]
  (let [type-1 (type-score hand-1)
        type-2 (type-score hand-2)]
    (if (not= type-1 type-2)
      (compare type-1 type-2)
      (compare (mapv card-score hand-1) (mapv card-score hand-2)))))

(defn winnings
  "Calculate the winnings associated with a ranked hand."
  [rank [_ bid]]
  (* (inc rank) (parse-long bid)))

(defn part-1
  ([]
   (part-1 input))
  ([data]
   (->> data
        str/split-lines
        (map #(str/split % #"\s+"))
        (sort-by first compare-hands)
        (map-indexed winnings)
        (apply +))))

(def card-score-2
  "A map of card characters to their relative score values for part 2."
  {\J 1
   \2 2
   \3 3
   \4 4
   \5 5
   \6 6
   \7 7
   \8 8
   \9 9
   \T 10
   \Q 12
   \K 13
   \A 14})

(defn best-use-of-joker
  "Given a hand, returns the card that any jokers should become to
  maximize the hand's rank."
  [hand]
  (let [[replacement _] (reduce (fn [[best best-count] [card count]]
                                  (cond (> count best-count)
                                        [card count]

                                        (and (= count best-count) (> (card-score-2 card) (card-score-2 best)))
                                        [card count]

                                        :else
                                        [best best-count]))
                                [\J 0]
                                (frequencies (str/replace hand "J" "")))]
       (str replacement)))

(defn apply-jokers
  "Given a hand, turns it into a hand of the best type possible given the
  in it."
  [hand]
  (str/replace hand "J" (best-use-of-joker hand)))

(defn compare-hands-2
  "A comparator that determines the relative ranks of a pair of hands,
  applying the joker rule from part 2."
  [hand-1 hand-2]
  (let [type-1 (type-score (apply-jokers hand-1))
        type-2 (type-score (apply-jokers hand-2))]
    (if (not= type-1 type-2)
      (compare type-1 type-2)
      (compare (mapv card-score-2 hand-1) (mapv card-score-2 hand-2)))))

(defn part-2
  ([]
   (part-2 input))
  ([data]
   (->> data
        str/split-lines
        (map #(str/split % #"\s+"))
        (sort-by first compare-hands-2)
        (map-indexed winnings)
        (apply +))))
