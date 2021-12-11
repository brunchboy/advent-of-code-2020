(ns advent-of-code-2015.day-19
  "Solutions for day 19."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(def molecule
  "The starting molecule in my problem statement."
  "CRnCaCaCaSiRnBPTiMgArSiRnSiRnMgArSiRnCaFArTiTiBSiThFYCaFArCaCaSiThCaPBSiThSiThCaCaPTiRnPBSiThRnFArArCaCaSiThCaSiThSiRnMgArCaPTiBPRnFArSiThCaSiRnFArBCaSiRnCaPRnFArPMgYCaFArCaPTiTiTiBPBSiThCaPTiBPBSiRnFArBPBSiRnCaFArBPRnSiRnFArRnSiRnBFArCaFArCaCaCaSiThSiThCaCaPBPTiTiRnFArCaPTiBSiAlArPBCaCaCaCaCaSiRnMgArCaSiThFArThCaSiThCaSiRnCaFYCaSiRnFYFArFArCaSiRnFYFArCaSiRnBPMgArSiThPRnFArCaSiRnFArTiRnSiRnFYFArCaSiRnBFArCaSiRnTiMgArSiThCaSiThCaFArPRnFArSiRnFArTiTiTiTiBCaCaSiRnCaCaFYFArSiThCaPTiBPTiBCaSiThSiRnMgArCaF")

(def replacements
  "The replacement rules in my problem statement."
  (-> (io/resource "2015/day_19.edn")
      slurp
      edn/read-string))

(defn part-1
  "Solve part 1"
  ([]
   (part-1 molecule replacements))
  ([molecule replacements]
   (loop [remaining replacements
          position  0
          found     #{}]
     (if (empty? remaining)
       (count found)
       (let [[target replacement] (first remaining)]
         (if-let [match (str/index-of molecule target position)]
           (recur remaining
                  (inc match)
                  (conj found (str (subs molecule 0 match) replacement (subs molecule (+ match (count target))))))
           (recur (rest remaining)
                  0
                  found)))))))

;; This doesn't terminate for the HOHOHO case, so it is going to need
;; to be a more sophisticated search algorithm trying alternatives.
(defn part-2
  "Fail to solve part 2."
  ([]
   (part-2 molecule replacements))
  ([molecule replacements]
   (let [sorted (sort-by #(- (count (second %))) replacements)]
     (loop [molecule  molecule
            remaining sorted
            count     0]
       (if (= molecule "e")
         count
         (if (empty? remaining)
           (recur molecule
                  sorted
                  count)
           (let [[replacement target] (first remaining)]
             (if (str/index-of molecule target)
               (recur (str/replace-first molecule target replacement)
                      remaining
                      (inc count))
               (recur molecule
                      (rest remaining)
                      count)))))))))

;; In the end I gave up trying to do a sophisticated search, and
;; studied the problem data to see if I could find patterns. There
;; were some promising looking ones: Most replacements just reduced
;; the number of chemicals by 1, so you would need as many steps as
;; there were chemicals in your target to get back to the starting
;; electron. However, if the pattern included Rn and Ar, there are
;; productions which remove both of those at the same time, so one
;; fewer step would be needed. Similarly, there are productions that
;; take away both elements on the side of a "Y", which acts like a
;; delimiter between them, so seeing a Y in the target reduces the
;; steps required by 2. Counting these special chemicals and doing the
;; math to predict the steps, rather than actually running them,
;; produced a correct answer.
(defn part-2-deeper
  "Solve part 2."
  []
  (let [tokenized (str/split molecule #"(?=[A-Z])")
        total     (count tokenized)
        bookends    (count (filter #{"Rn" "Ar"} tokenized))
        delims    (count (filter #{"Y"} tokenized))]
    [total bookends delims]
    (- total bookends (* 2 delims) 1)))
