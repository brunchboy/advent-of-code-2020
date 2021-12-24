(ns advent-of-code-2021.day-24
  "Solutions for day 24."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def program
  "The puzzle input"
  (-> (io/resource "2021/day_24.txt")
      slurp))

(defn inp
  [alu register]
  (-> alu
      (assoc-in [:registers register] (first (:inputs alu)))
      (update :inputs rest)))

(defn resolve-operand
  [alu operand]
  (if (re-matches #"(\+|-)?\d+" operand)
    (Long/parseLong operand)
    (get-in alu [:registers operand] 0)))

(defn update-with-fn
  [alu register operand fn]
  (-> alu
      (update-in [:registers register] (fnil fn 0) (resolve-operand alu operand))))

(defn execute
  [alu instruction]
  (let [[op & args] (str/split instruction #"\s+")]
    (case op
      "inp"
      (inp alu (first args))

      "add"
      (update-with-fn alu (first args) (second args) +)

      "mul"
      (update-with-fn alu (first args) (second args) *)

      "div"
      (update-with-fn alu (first args) (second args) quot)

      "mod"
      (update-with-fn alu (first args) (second args) mod)

      "eql"
      (update-with-fn alu (first args) (second args) (fn [n1 n2] (if (= n1 n2) 1 0))))))

(defn run
  [alu program]
  (reduce (fn [alu instruction]
            (execute alu instruction))
          alu
          (str/split-lines program)))

(defn part-1
  []
  (loop [candidate 99999999999999]
    (let [inputs (map #(Long/parseLong %) (str/split (str candidate) #""))]
      (if (some zero? inputs)
        (recur (dec candidate))
        (let [alu (run {:inputs inputs} program)]
          (when (= 9999 (mod candidate 10000))
            (println candidate)
            (println alu))
          (if (zero? (get-in alu [:registers "z"]))
            candidate
            (recur (dec candidate))))))))

;; This works, but far far too slowly to reach the answer. So I ended
;; up reverse-engineering the machine code of the ALU program that
;; made up my problem. It consists of fourteen copies of an
;; eighteen-line block which reads an input value and does one of two
;; things with it:
;;
;; Line 5 of the block divides z either by 26, or by 1 (a no-op).
;; There are seven of each kind of block in the program. Let's first
;; consider the cases where it divides by 1.
;;
;; The code starts by reading input into w, then it sets up x to hold
;; z modulo 26. It then divides z by 1 (doing nothing), and adds a
;; value greater than 10 to x. This means that x can never equal w, so
;; lines 7 and 8 sets x to 1. (x becomes a flag indicating that the
;; input was not matched). When the input is not matched, y gets set
;; to 25, incremented, and z gets multiplied by y. (I call the
;; constant that gets added to the input w in line 6 `sub`, for
;; reasons I will explain in the section describing the other variant
;; of the code block below). So we end up multiplying z by 26. Lines
;; 14-16 then set y equal to the input plus another constant that I
;; call `add`, which gets zeroed out if the input matched in line 17
;; (the zeroing can never happen in this variant), and that gets added
;; to z.
;;
;; So what that all boils down to is that z is being used as a stack
;; of values, modulo 26. This variant of the code block will always
;; push a new value on the stack, and that value is equal to the
;; current input plus the constant `sub` found on line 6.
;;
;; In the second variant, where line 5 divides z by 26, we are popping
;; a value off the stack, and we have control over what happens. The
;; way the math works out is if the current input plus `add` is equal
;; to the popped-off value (which works out to the pushed input minus
;; `sub`) then we will not push a new value onto the stack.
;;
;; So the only way we can end up with a zero in z is if in every one
;; of the blocks of code, the value we input at that point equals the
;; value input during the code block that pushed it on the stack, plus
;; `add`, minus `sub`. The pushes and pops balance out in the end, and
;; there is always a pushed value to be popped when necessary. We can
;; then write a set of equations describing relationsips that need to
;; be true between pairs of input values which can be used to create
;; legal model numbers, and maximize or minimize the total value.
;;
;; With the values of `add` and `sub` in my own problem input, I saw
;; the following results. (I put angle brackets around <add> and <sub>
;; when those values are not going to be used, because `sub` is
;; irrelevant in stages that always push, and `add` is ignored when a
;; successful match avoids pushing.) I show the contents of the stack
;; as a vector, with the most recent popped value in front of it on a
;; pop stage.

;; i[0]: push [0]; <sub=-12>, add=4
;; i[1]: push [1 0]; <sub=-15>, add=11
;; i[2]: push [2 1 0]; <sub=-11>, add=7
;; i[3]: pop 2 [1 0]; sub=14, <add=2>  => i[2] + 7 - 14 = i[3] => i[2] - 7 = i[3]
;; i[4]: push [4 1 0]; <sub=-12>, add=11
;; i[5]: pop 4 [1 0]; sub=10, <add=13> => i[4] + 11 - 10 = i[5] => i[4] + 1 = i[5]
;; i[6]: push [6 1 0]; <sub=-11>, add=9
;; i[7]: push [7 6 1 0]; <sub=-13>, add=12
;; i[8]: pop 7 [6 1 0]; sub=7, <add=6> => i[7] + 12 - 7 = i[8] => i[7] + 5 = i[8]
;; i[9]: push [9 6 1 0]; <sub=-10>, add=2
;; i[10]: pop 9 [6 1 0]; sub=2, <add=11> => i[9] + 2 - 2 = i[10] => i[9] = i[10]
;; i[11]: pop 6 [1 0]; sub=1, <add=12> => i[6] + 9 - 1 = i[11] => i[6] + 8 = i[11]
;; i[12]: pop 1 [0]; sub=4, <add=3> => i[1] + 11 - 4 = i[12] => i[1] + 7 = i[12]
;; i[13]: pop 0 []; sub=12, <add=13> => i[0] + 4 - 12 = i[13] => i[0] - 8 = i[13]

;; For maximum:
;; i[ 0] = 9
;; i[ 1] = 2
;; i[ 2] = 9
;; i[ 3] = 2
;; i[ 4] = 8
;; i[ 5] = 9
;; i[ 6] = 1
;; i[ 7] = 4
;; i[ 8] = 9
;; i[ 9] = 9
;; i[10] = 9
;; i[11] = 9
;; i[12] = 9
;; i[13] = 1

;; For minimum:
;; i[ 0] = 9
;; i[ 1] = 1
;; i[ 2] = 8
;; i[ 3] = 1
;; i[ 4] = 1
;; i[ 5] = 2
;; i[ 6] = 1
;; i[ 7] = 1
;; i[ 8] = 6
;; i[ 9] = 1
;; i[10] = 1
;; i[11] = 9
;; i[12] = 8
;; i[13] = 1
