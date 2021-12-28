(ns advent-of-code-2015.day-21
  "Solutions for day 21."
  (:require [clojure.math.combinatorics :as combo]))

(def boss
  "The puzzle input: the boss stats."
  {:hit-points 103
   :damage     9
   :armor      2})

(def weapons
  "The available weapons."
  [{:name   "Dagger"
    :cost   8
    :damage 4
    :armor  0}
   {:name   "Shortsword"
    :cost   10
    :damage 5
    :armor  0}
   {:name   "Warhammer"
    :cost   25
    :damage 6
    :armor  0}
   {:name   "Longsword"
    :cost   40
    :damage 7
    :armor  0}
   {:name   "Greataxe"
    :cost   74
    :damage 8
    :armor  0}])

(def armor
  "The available armor."
  [{:name   "None"
    :cost   0
    :damage 0
    :armor  0}
   {:name   "Leather"
    :cost   13
    :damage 0
    :armor  1}
   {:name   "Chainmail"
    :cost   31
    :damage 0
    :armor  2}
   {:name   "Splintmail"
    :cost   53
    :damage 0
    :armor  3}
   {:name   "Bandedmail"
    :cost   75
    :damage 0
    :armor  4}
   {:name   "Platemail"
    :cost   102
    :damage 0
    :armor  5}])

(def rings
  "The available rings."
  [{:name   "None (left)"
    :cost   0
    :damage 0
    :armor  0}
   {:name   "None (right)"
    :cost   0
    :damage 0
    :armor  0}
   {:name      "Damage +1"
    :cost   25
    :damage 1
    :armor  0}
   {:name   "Damage +2"
    :cost   50
    :damage 2
    :armor  0}
   {:name   "Damage +3"
    :cost   100
    :damage 3
    :armor  0}
   {:name   "Defense +1"
    :cost   20
    :damage 0
    :armor  1}
   {:name   "Defense +2"
    :cost   40
    :damage 0
    :armor  2}
   {:name   "Defense +3"
    :cost   80
    :damage 0
    :armor  3}])

(defn turns-to-kill
  "Calculate how many turns an attacker will take to defeat a defender."
  [attacker defender]
  (long (Math/ceil (/ (:hit-points defender) (max 1 (- (:damage attacker) (:armor defender)))))))

(defn player-wins?
  "Determine whether the player will win a battle with the boss as
  currently equipped."
  [player boss]
  (>= (turns-to-kill boss player) (turns-to-kill player boss)))

(defn part-1
  "Solve part 1."
  []
  (apply min
         (for [weapon       weapons
               armor-chosen armor
               rings-chosen (combo/combinations rings 2)]
           (let [equipped (concat [weapon armor-chosen] rings-chosen)]
             (if (player-wins? {:hit-points 100
                               :armor (apply + (map :armor equipped))
                               :damage (apply + (map :damage equipped))}
                               boss)
               (apply + (map :cost equipped))
               Long/MAX_VALUE)))))

(defn part-2
  "Solve part 2."
  []
  (apply max
         (for [weapon       weapons
               armor-chosen armor
               rings-chosen (combo/combinations rings 2)]
           (let [equipped (concat [weapon armor-chosen] rings-chosen)]
             (if (player-wins? {:hit-points 100
                               :armor (apply + (map :armor equipped))
                               :damage (apply + (map :damage equipped))}
                               boss)
               0
               (apply + (map :cost equipped)))))))
