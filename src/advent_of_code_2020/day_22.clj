(ns advent-of-code-2020.day-22
  "Solutions for day 22."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn read-hand
  "Reads a single player's hand in the format of the puzzle input into a
  list of integers."
  [hand]
  (->> hand
       str/split-lines
       rest
       (map #(Long/parseLong %))))

(def input
  "The starting hands (puzzle input). Parses the input into the lists of
  cards held by each player."
  (->> (str/split (slurp (io/resource "day_22.txt"))
                  #"\n\n")
      (map read-hand)))

(defn turn
  "Play one round of the game. Whichever player turns over the higher
  card, places that card followed by the card played by the other
  player onto the bottom of their own deck. Return the resulting
  decks."
  [[[card-1 & hand-1] [card-2 & hand-2]]]
  (if (> card-1 card-2)
    [(concat hand-1 [card-1 card-2]) hand-2]
    [hand-1 (concat hand-2 [card-2 card-1])]))

(defn play
  "Keep playing turns until one player is out of cards. Return the
  resulting decks."
  [hands]
  (first (drop-while #(every? seq %) (iterate turn hands))))

(defn part-1
  "Solve part 1 of the puzzle, returning the winning player's deck value
  as described in the problem statement: value of the bottom card * 1
  + next card * 2, + next card * 3...

  The starting hands can be specified for unit testing with the sample
  data."
  ([]
   (part-1 input))
  ([hands]
   (let [winner (first (filter seq (play hands)))]
     (apply + (map-indexed (fn [i v] (* (inc i) v))
                           (reverse winner))))))

(declare play-2)  ; They did say this was recursive combat!

(defn turn-2
  "Play one round of the recursive game. Each player turns over a card.
  If there are enough cards in each player's deck, create copies of as
  many subsequent cards as specified by the revealed card, and play a
  new game with those. The winner of the recursive game gets both
  turned-over cards added to the bottom of their own deck, starting
  with their own card. If at any point we return to a configuration of
  cards that we have seen before in this game, the first player
  instantly wins."
  [[hand-1 hand-2 seen]]
  (if (seen [hand-1 hand-2])
    [hand-1 [] seen]  ; First player wins because we have seen this configuration before.
    (let [seen (conj seen [hand-1 hand-2])  ; Record this configuration in case we get back to it.
          [card-1 & hand-1] hand-1
          [card-2 & hand-2] hand-2]
      (if (and (>= (count hand-1) card-1)  ; If there are enough cards, play the recursive game.
               (>= (count hand-2) card-2))
        (let [[subhand-1 _subhand-2] (play-2 [(take card-1 hand-1) (take card-2 hand-2)])]
          (if (seq subhand-1)  ; See which player won the recursive game.
            [(concat hand-1 [card-1 card-2]) hand-2 seen]
            [hand-1 (concat hand-2 [card-2 card-1]) seen]))
        (if (> card-1 card-2)  ; Not enough cards, play a normal round.
          [(concat hand-1 [card-1 card-2]) hand-2 seen]
            [hand-1 (concat hand-2 [card-2 card-1]) seen])))))

(defn play-2
  "Keep playing turns of the recursive version of the game until one
  player is out of cards. Return the resulting decks. Sets up the
  initial `seen` state to reflect that we have not previously seen any
  hands."
  ([hands]
   (play-2 hands #{}))
  ([hands seen]
   #_(println "play-2" hands seen)
   (butlast (first (drop-while #(every? seq (butlast %)) (iterate turn-2 (concat hands [seen])))))))

(defn part-2
  "Solve part 2 of the puzzle, computing the value of the winner's hand
  as before.

  The starting hands can be specified for unit testing with the sample
  data."
  ([]
   (part-2 input))
  ([hands]
   (let [winner (first (filter seq (play-2 hands)))]
     (apply + (map-indexed (fn [i v] (* (inc i) v))
                           (reverse winner))))))
