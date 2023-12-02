(ns advent-of-code-2023.day-2
  "Solution for day 2."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [instaparse.core :as insta]))


(def sample-data
  "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green")

(defn parse-round
  "Parse the information about colors and ball counts from a single round
  of a game into a map from color string to count."
  [s]
  (reduce (fn [acc s]
            (let [[n color] (str/split s #" ")]
              (assoc acc color (parse-long n))))
          {}
          (str/split s #", ")))

(defn parse-game
  "Parse a game number and gather maps describing the balls found in each
  round of that game."
  [s]
  (let [[title rounds] (str/split s #": ")
        game           (parse-long (second (str/split title #" ")))]
    [game (reduce #(merge-with max %1 %2) (map parse-round (str/split rounds #"; ")))]))

(defn possible-game
  "Checks whether a game uses no more than the number of balls of each
  color known to be available."
  [game]
  (let [colors (second game)]
    (and (>= 12 (get colors "red" 0))
         (>= 13 (get colors "green" 0))
         (>= 14 (get colors "blue" 0)))))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2023/day_2.txt")
       slurp))

(defn part-1
  "Solve part 1 of the puzzle."
  ([]
   (part-1 input))
  ([games]
   (->> games
        str/split-lines
        (map parse-game)
        (filter possible-game)
        (map first)
        (apply +))))

(defn calculate-power
  "Given a game map, calculate the power of that game according to the rules."
  [game]
  (apply * (vals (second game))))

(defn part-2
  "Solve part 2 of the puzzle."
  ([]
   (part-2 input))
  ([games]
   (->> games
        str/split-lines
        (map parse-game)
        (map calculate-power)
        (apply +))))

;;; An alternate approach, using instaparse

(def instaparse-games
  "Create a parser for the game list format."
  (insta/parser (io/resource "2023/day_2.bnf")))

(defn eval-round
  "Given a parsed game round, translate it into a map from color string
  to ball count."
  [rounds]
  (reduce (fn [acc [num color]]
            (assoc acc color (parse-long num)))
          {}
          (partition 2 (rest rounds))))

(defn eval-games
  "Given a parse of the game list, convert it into a list of tuples of
  game number and maps of the maximum number of balls of each color
  seen in the rounds of that game."
  [games]
  (reduce (fn [acc [_ num & rounds]]
            (assoc acc (parse-long num) (reduce #(merge-with max %1 %2) (map eval-round rounds))))
          {}
          (rest games)))

(defn v2-part-1
  "Solve part 1 of the problem using instaparse."
  ([]
   (v2-part-1 input))
  ([games]
   (->> games
        instaparse-games
        eval-games
        (filter possible-game)
        (map first)
        (apply +))))

(defn v2-part-2
  "Sovle part 2 of the problem using instaparse."
  ([]
   (v2-part-2 input))
  ([games]
   (->> games
        instaparse-games
        eval-games
        (map calculate-power)
        (apply +))))
