(ns advent-of-code-2015.day-18
  "Solutions for day 18."
  (:require [clojure.java.io :as io]))

(def input
  "The starting light configuration."
  (->> (io/resource "2015/day_18.txt")
       io/reader
       line-seq
       vec))

(defn lit?
  "Checks whether the light at the specified coordinates is illuminated;
  going off the edge of the layout means it is unlit."
  [layout x y]
  (= (get-in layout [y x] \.) \#))

(defn count-neighbors
  "Counts how many lights adjacent to the specified location are lit."
  [layout x y]
  (apply +
         (for [dx (range -1 2)
               dy (range -1 2)]
           (if (and (zero? dx) (zero? dy))
             0
             (if (lit? layout (+ x dx) (+ y dy))
               1
               0)))))

(defn step
  "Generates the next light configuration after the supplied one, given
  the generation rules."
  [layout]
  (vec
   (for [y (range (count layout))]
     (apply str
            (for [x (range (count (nth layout y)))]
              (let [current (get-in layout [y x])]
                (case current
                  \.
                  (if (= 3 (count-neighbors layout x y)) \# \.)

                  \#
                  (if (#{2 3} (count-neighbors layout x y)) \# \.)

                  current)))))))

(defn count-lit
  "Counts how many lights are lit in the supplied configuration."
  [layout]
  (count (filter #{\#} (apply str layout))))

(defn part-1
  "Solve part 1."
  []
  (count-lit (first (drop 100 (iterate step input)))))

(defn step-2
  "Generates the next light configuration after the supplied one, given
  the generation rules with the four corners stuck on."
  [layout]
  (vec
   (for [y (range (count layout))]
     (apply str
            (for [x (range (count (nth layout y)))]
              (if (and (or (zero? x) (= x (dec (count (nth layout y)))))
                       (or (zero? y) (= y (dec (count layout)))))
                \#  ; A corner, so stuck on regardless of neighbors.
                (let [current (get-in layout [y x])]
                  (case current
                    \.
                    (if (= 3 (count-neighbors layout x y)) \# \.)

                    \#
                    (if (#{2 3} (count-neighbors layout x y)) \# \.)

                    current))))))))

(defn part-2
  "Solve part 2."
  []
  (count-lit (first (drop 100 (iterate step-2 input)))))
