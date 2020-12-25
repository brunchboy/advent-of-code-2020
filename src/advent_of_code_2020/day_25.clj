(ns advent-of-code-2020.day-25
  "Solutions for day 24."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def card-public-key
  "First part of puzzle input."
  3418282)

(def door-public-key
  "Second part of puzzle input."
  8719412)

(defn find-loop-size
  "A basic brute-force search for the number of iterations it takes to
  transform the starting value to reach the specified key. I was
  worried this was going to be far too slow, and would need to revisit
  the space cards puzzle from 2019 to figure out some higher modular
  math tricks, but for my puzzle input the millions of iterations took
  less than quarter second for the worst (door) case."
  [key]
  (loop [i 0
         v 1]
    (if (= v key)
      i
      (recur (inc i)
             (mod (* v 7) 20201227)))))

(def card-loop-size
  "Determined by calling find-loop-size with`card-public-key`."
  8987376)

(def door-loop-size
  "Determined by calling find-loop-size with`door-public-key`."
  14382089)

(defn transform
  "Perform the transformation operation specified in the problem
  statement with a given loop size and public key to determine the
  encryption key."
  [loop-size public-key]
  (loop [i 0
         v 1]
    (if (= i loop-size)
      v
      (recur (inc i)
             (mod (* v public-key) 20201227)))))

(defn part-1
  "Solve the first (and only) part of this final puzzle."
  []
  (transform card-loop-size door-public-key))
