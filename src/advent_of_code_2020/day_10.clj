(ns advent-of-code-2020.day-10
  "Solutions for day 10.")

(def input
  "The actual adapters making up the problem."
  [114
   51
   122
   26
   121
   90
   20
   113
   8
   138
   57
   44
   135
   76
   134
   15
   21
   119
   52
   118
   107
   99
   73
   72
   106
   41
   129
   83
   19
   66
   132
   56
   32
   79
   27
   115
   112
   58
   102
   64
   50
   2
   39
   3
   77
   85
   103
   140
   28
   133
   78
   34
   13
   61
   25
   35
   89
   40
   7
   24
   33
   96
   108
   71
   11
   128
   92
   111
   55
   80
   91
   31
   70
   101
   14
   18
   12
   4
   84
   125
   120
   100
   65
   86
   93
   67
   139
   1
   47
   38])

(defn part-1
  "Solve part 1, using a simple sort and frequency count."
  [adapters]
  (let [adapters (sort adapters)
        device   (+ (last adapters) 3)
        jolts    (sort (conj adapters 0 device))
        counts   (->> jolts
                      (partition 2 1)
                      (map #(apply - %))
                      frequencies)]
    (* (get counts -1 0) (get counts -3 0))))

(defn tribonacci
  "Simplistic implementation of the tribonacci sequence; we don't need
  to expand it very far."
  [n]
  (cond
    (<= n 1)
    1

    (= n 2)
    2

    (= n 3)
    4

    :else (+ (tribonacci (dec n)) (tribonacci (- n 2)) (tribonacci (- n 3)))))

(defn part-2
  "Solve part 2, by finding the places where permutations are possible,
  and counting them using the tribonacci sequence."
  [adapters]
  (let [adapters (sort adapters)
        device (+ (last adapters) 3)]
    (->> (concat [0] adapters [device])
         (partition 2 1)
         (map #(apply - %))
         (partition-by (partial = -3))
         (remove #(< (first %) -1))
         (map count)
         (map tribonacci)
         (apply *))))
