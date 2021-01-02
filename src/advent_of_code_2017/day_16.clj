(ns advent-of-code-2017.day-16
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def input
  "The dance moves (puzzle input)."
  (-> "2017/day_16.txt"
      io/resource
      slurp
      str/trim
      (str/split #",")))

(defn move
  "Applies a movement command as defined in the problem statement to the
  current list of programs."
  [progs cmd]
  (or
   (when-let [[_ size] (re-matches #"s(\d+)" cmd)]
     (let [split (- (count progs) (Long/parseLong size))]
       (str (subs progs split) (subs progs 0 split) )))
   (when-let [[_ a b] (re-matches #"x(\d+)/(\d+)" cmd)]
     (let [a (Long/parseLong a)
           b (Long/parseLong b)
           progs (vec progs)]
       (apply str (-> progs
                      (assoc a (nth progs b))
                      (assoc b (nth progs a))))))
   (when-let [[_ a b] (re-matches #"p(.)/(.)" cmd)]
     (move progs (str "x" (.indexOf progs a) "/" (.indexOf progs b))))
   (throw (Exception. (str "Unrecognized move command format: " cmd)))))

(defn dance
  "Applies the list of moves to the input list of programs in order, and
  returns the resulting arrangement."
  [progs moves]
  (reduce (fn [acc cmd]
            (move acc cmd))
          progs
          moves))

(defn part-1
  "Solve part 1 of the problem."
  []
  (dance "abcdefghijklmnop" input))

(def after-dance-indices
  "The index from which each element of the original program list was
  pulled to get to the result of an entire dance from part 1. This can
  be used to iterate the dance efficiently."
  (mapv #(- (int %) (int \a)) (part-1)))

(defn find-cycle-size
  "Going to assume we get back to the starting state in a sane number of
  moves otherwise I am not sure how to solve this in a reasonable
  amount of time. So let's see..."
  [rounds]
  (loop [rounds (drop 1 rounds)
         n      1]
    (if (= (first rounds) "abcdefghijklmnop")
      n
      (recur (drop 1 rounds)
             (inc n)))))

(defn part-2
  "Hurrah! It turns out there is a tiny cycle syze of 60, so we don't
  need to do anything like a billion iterations."
  []
  (let [rounds (iterate #(dance % input) "abcdefghijklmnop")]
    (nth rounds (mod 1000000000 60 #_(find-cycle-size rounds)))))
