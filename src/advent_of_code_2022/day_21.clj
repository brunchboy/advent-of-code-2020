(ns advent-of-code-2022.day-21
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_21.txt")
       slurp
       str/split-lines))

(defn resolve-value
  "Once we have found a value for a monkey, replace each instance of that
  monkey with the value."
  [k v lines]
  (mapv #(str/replace % k v) lines))

(defn propagate-constants
  "Find all monkeys whose value is known and resolve them."
  [lines]
  (loop [[line & remaining] lines
         result             []]
    (if-not line
      result
      ;; I originally had a simpler expression to match solved numbers
      ;; because I was just using integers, but that turned out not to
      ;; work, due to both fractional and huge results. I was worried
      ;; floating point would lack sufficient precision, but it turned
      ;; out fine.
      (if-let [[_ k v] (re-matches #"([a-z]+): (-?\d+\.?\d*E?\d*)" line)]
        (recur (resolve-value k v remaining)
               (resolve-value k v result))
        (recur remaining
               (conj result line))))))

(defn solve-equations
  "Find all monkeys whose expressions constants have both been resolved,
  and compute the resulting value, replacing the expression with it."
  [lines]
  (loop [[line & remaining] lines
         result             []]
    (if-not line
      result
      (if-let [[_ k x op y] (re-matches #"([a-z]+): (-?\d+\.?\d*E?\d*) (.) (-?\d+\.?\d*E?\d*)" line)]
        (recur remaining
               (conj result (str k ": " ((resolve (symbol op)) (Double/parseDouble x)
                                         (Double/parseDouble y)))))
        (recur remaining
               (conj result line))))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (loop [data data]
     (if-let [[_ v] (re-matches #"root: (-?\d+\.\d*E?\d*)" (first data))]
       v
       (recur (->> data propagate-constants solve-equations))))))

(defn part-2
  "Solve part 2. I realized I could just replace the `+` with a `-` in
  the `root` expression and solve for 0. I did some manual bracketing
  by editing the `humn` line in the input file, which is where the
  `max` and `min` constants below came from. This function just
  performs a binary search for the solution."
  ([]
   (let [lines (->> (io/resource "2022/day_21_b.txt")
                    slurp
                    str/split-lines)]
     (loop [max 3296151343750
            min 3296116187500]
       ;; Inject the mid-point of our search as the `humn` value and run the solver.
       (let [attempt (quot (+ max min) 2)
             result  (Double/parseDouble (part-1 (conj lines (str "humn: " attempt))))]
         (println max attempt min result)
         (if (or (zero? result) (= max min))
           [attempt result]
           (if (neg? result)
             (recur attempt min)
             (recur max attempt)))))))
  ([data]
   (part-1 data)))

(def sample-input
  "The test data."
  (->> "root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32"
       str/split-lines))

(def sample-input-2
  "Modification of the test data to experiment with feasibility of binary
  search."
  (->> "root: pppw - sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 301
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32"
       str/split-lines))
