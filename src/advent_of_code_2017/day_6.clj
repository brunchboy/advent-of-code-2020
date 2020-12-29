(ns advent-of-code-2017.day-6)

(def input
  "The starting list of memory bank block counts (puzzle input)."
  [0 5 10 0 11 14 13 4 11 8 8 7 1 4 12 11])

(defn start-redistribution
  "Given a list of memory bank block counts, Finds the memory bank with
  the largest number of blocks (breaking ties by choosing the bank
  with the lowest index). Returns a tuple of the block counts with
  that bank zeroed out, its index, and the number of blocks to be
  redistributed."
  [banks]
  (let [[blocks index] (->> (interleave banks (map - (range)))
                            (partition 2)
                            (map vec)
                            (sort (comp - compare))
                            first)
        index          (- index)] ; We used negative indices to sort
                                  ; ties according to the rules.
    [(assoc banks index 0) blocks index]))

(defn redistribution-step
  "Performs one step in the redistribution of memory blocks. Takes the
  structure returned by `start-distribution`, distributes one block to
  the next index, decrements the blocks remaining to be redistributed,
  increments the index (wrapping at the end of the blocks), and
  returns the updated structure."
  [[banks blocks index]]
  (let [index (mod (inc index) (count banks))]
    [(update banks index inc) (dec blocks) index]))

(defn run-redistribution-cycle
  "Performs a distribution cycle on the memory banks. Given a list of
  block counts, chooses the bank to have its contents redistributed,
  then iterates until there are no blocks left to redistribute from
  it. Returns the resulting block counts."
  [banks]
  (->> (iterate redistribution-step (start-redistribution banks))
       (drop-while (fn [[_ blocks]] (pos? blocks)))
       first
       first))

(defn part-1
  "Run redistribution cycles until we reach a state we have seen before,
  then report how many cycles it took."
  [banks]
  (loop [banks  banks
         seen   #{}
         cycles 0]
    (if (seen banks)
      cycles
      (recur (run-redistribution-cycle banks)
             (conj seen banks)
             (inc cycles)))))

(defn part-2
  "Run redistribution cycles until the second time reach a state we have seen before,
  then report how many cycles it took between the first and second
  times."
  [banks]
  (let [looped-state (loop [banks  banks
                            seen   #{}]
                       (if (seen banks)
                         banks
                         (recur (run-redistribution-cycle banks)
                                (conj seen banks))))]
    (loop [banks  looped-state
           cycles 0]
      (if (and (pos? cycles) (= banks looped-state))
        cycles
        (recur (run-redistribution-cycle banks)
               (inc cycles))))))
