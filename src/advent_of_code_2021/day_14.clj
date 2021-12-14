(ns advent-of-code-2021.day-14
  "Solutions for day 14.")

(def template
  "The starting seed for our polymer (puzzle input part 1)."
  "OOFNFCBHCKBBVNHBNVCP")

(def rules
  "The insertion rules for growing our polymer (puzzle input part 2)."
  {"PH" "V"
   "OK" "S"
   "KK" "O"
   "BV" "K"
   "CV" "S"
   "SV" "C"
   "CK" "O"
   "PC" "F"
   "SC" "O"
   "KC" "S"
   "KF" "N"
   "SN" "C"
   "SF" "P"
   "OS" "O"
   "OP" "N"
   "FS" "P"
   "FV" "N"
   "CP" "S"
   "VS" "P"
   "PB" "P"
   "HP" "P"
   "PK" "S"
   "FC" "F"
   "SB" "K"
   "NC" "V"
   "PP" "B"
   "PN" "N"
   "VN" "C"
   "NV" "O"
   "OV" "O"
   "BS" "K"
   "FP" "V"
   "NK" "K"
   "PO" "B"
   "HF" "H"
   "VK" "S"
   "ON" "C"
   "KH" "F"
   "HO" "P"
   "OO" "H"
   "BC" "V"
   "CS" "O"
   "OC" "B"
   "VB" "N"
   "OF" "P"
   "FK" "H"
   "OH" "H"
   "CF" "K"
   "CC" "V"
   "BK" "O"
   "BH" "F"
   "VV" "N"
   "KS" "V"
   "FO" "F"
   "SH" "F"
   "OB" "O"
   "VH" "F"
   "HH" "P"
   "PF" "C"
   "NF" "V"
   "VP" "S"
   "CN" "V"
   "SK" "O"
   "FB" "S"
   "FN" "S"
   "BF" "H"
   "FF" "V"
   "CB" "P"
   "NN" "O"
   "VC" "F"
   "HK" "F"
   "BO" "H"
   "KO" "C"
   "CH" "N"
   "KP" "C"
   "HS" "P"
   "NP" "O"
   "NS" "V"
   "NB" "H"
   "HN" "O"
   "BP" "C"
   "VF" "S"
   "KN" "P"
   "HC" "C"
   "PS" "K"
   "BB" "O"
   "NO" "N"
   "NH" "F"
   "BN" "F"
   "KV" "V"
   "SS" "K"
   "CO" "H"
   "KB" "P"
   "FH" "C"
   "SP" "C"
   "SO" "V"
   "PV" "S"
   "VO" "O"
   "HV" "N"
   "HB" "V"})

(defn step
  "Apply the insertion rules once, growing the polymer wherever
  possible, as described in the problem statement."
  [rules template]
  (str (->> (for [i (range (dec (count template)))]
              (let [[c1 c2] (drop i template)]
                (str c1 (rules (str c1 c2)))))
            (apply str))
       (last template)))

(defn part-1
  "Solve part 1, growing the polymer ten steps, and finding the
  difference between the count of the most and least common elements
  in the result."
  ([]
   (part-1 rules template))
  ([rules template]
   (let [polymer (->> (iterate (partial step rules) template)
                      (drop 10)
                      first)
         freq    (frequencies polymer)]
     (- (apply max (vals freq)) (apply min (vals freq))))))

(defn step-2
  "Apply the insertion rules once, without tracking the actual polymer
  string, working from (and updating) the counts of distinct pairs and
  individual elements instead."
  [rules [letters pairs]]
  (loop [left    pairs
         letters letters
         pairs   {}]
    (if-let [[pair count] (first left)]
      (if-let [inserted (get rules pair)]
        (recur (rest left)
               (update letters inserted (fnil + 0) count)
               (-> pairs
                   (update (str (first pair) inserted) (fnil + 0) count)
                   (update (str inserted (second pair)) (fnil + 0) count)))
        (recur (rest left)
               letters
               (assoc pairs pair count)))
      [letters pairs])))

(defn part-2
  "Solve part 2, growing the polymer forty steps, and finding the
  difference between the count of the most and least common elements
  in the result."
  ([]
   (part-2 rules template))
  ([rules template]
   (let [letters (frequencies (map str template))
         pairs   (frequencies (map #(apply str %) (partition 2 1 template)))
         polymer (->> (iterate (partial step-2 rules) [letters pairs])
                      (drop 40)
                      first)
         letters (first polymer)]
     (- (apply max (vals letters)) (apply min (vals letters))))))
