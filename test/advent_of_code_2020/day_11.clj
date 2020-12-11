(ns advent-of-code-2020.day-11
  "Solutions for day 11."
  (:require [clojure.java.io :as io]))

(def input
  "The ferry seat layout."
  (->> (io/resource "day_11.txt")
       io/reader
       line-seq
       vec))

(defn occupied?
  "Checks whether the seat at the specified coordinates is occupied;
  going off the edge of the layout means it is unoccupied."
  [layout x y]
  (= (get-in layout [y x] \.) \#))

(defn count-neighbors
  "Counts how many seats adjacent to the specified location are
  occupied."
  [layout x y]
  (apply +
         (for [dx (range -1 2)
               dy (range -1 2)]
           (if (and (zero? dx) (zero? dy))
             0
             (if (occupied? layout (+ x dx) (+ y dy))
               1
               0)))))


(defn step
  "Generates the next seat layout after the supplied one, given the
  generation rules."
  [layout]
  (vec
   (for [y (range (count layout))]
     (apply str
            (for [x (range (count (nth layout y)))]
              (let [current (get-in layout [y x])]
                (case current
                  \L
                  (if (zero? (count-neighbors layout x y)) \# \L)

                  \#
                  (if (>= (count-neighbors layout x y) 4) \L \#)

                  current)))))))

(defn part-1
  "Iterate the rules until we reach a steady state and then count the
  occupied seats."
  [layout]
  (->>
   (reduce #(if (= %1 %2) (reduced %1) %2) (iterate step layout))
   (apply str)
   (filter #(= \# %))
   count))

(defn first-seat
  "Finds the first seat in the specified direction from the specified
  seat; going off the edge of the layout is treated as finding an
  empty seat."
  [layout x y dx dy]
  (let [x       (+ x dx)
        y       (+ y dy)
        current (get-in layout [y x] \L)]
    (if (= current \.)
      (recur layout x y dx dy)
      current)))

(defn count-neighbors-2
  "Counts how many of the first seat visible in any direction from the
  specified location are occupied."
  [layout x y]
  (apply +
         (for [dx (range -1 2)
               dy (range -1 2)]
           (if (and (zero? dx) (zero? dy))
             0
             (if (= \# (first-seat layout x y dx dy))
               1
               0)))))

(defn step-2
  "Generates the next seat layout after the supplied one, given the
  generation rules for part 2."
  [layout]
  (vec
   (for [y (range (count layout))]
     (apply str
            (for [x (range (count (nth layout y)))]
              (let [current (get-in layout [y x])]
                (case current
                  \L
                  (if (zero? (count-neighbors-2 layout x y)) \# \L)

                  \#
                  (if (>= (count-neighbors-2 layout x y) 5) \L \#)

                  current)))))))

(defn part-2
  "Iterate the part 2 adjusted rules until we reach a steady state and
  then count the occupied seats."
  [layout]
  (->>
   (reduce #(if (= %1 %2) (reduced %1) %2) (iterate step-2 layout))
   (apply str)
   (filter #(= \# %))
   count))
