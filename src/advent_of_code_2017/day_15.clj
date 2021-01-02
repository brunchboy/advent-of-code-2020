(ns advent-of-code-2017.day-15)

(defn generator-step
  "Calculates the next value for a generator function with the given
  factor and current value."
  [factor n]
  (mod (* n factor) 2147483647))

(defn build-generator
  "Creates a generator sequence using the formula described in the
  problem statement."
  [factor start]
  (drop 1 (iterate (partial generator-step factor) start)))

(defn low-16-bits-match?
  "Checks whether the sixteen low-order bits of the supplied numbers are
  the same."
  [a b]
  (= (bit-and a 0xffff) (bit-and b 0xffff)))

(defn build-generator-a
  "Create the first of the two generators specified by the problem
  statement and problem input."
  []
  (build-generator 16807 699))

(defn build-generator-b
  "Create the second of the two generators specified by the problem
  statement and problem input."
  []
  (build-generator 48271 124))

(defn part-1
  "Solves part 1 of the problem by counting how often the generators'
  sixteen low-order bits match in 40 million iterations."
  []
  (count (filter identity (take 40000000 (map low-16-bits-match? (build-generator-a) (build-generator-b))))))

(defn part-2
  "Solves part 2 of the problem by filtering each of the generators as
  described in the problem statement and counting how often the
  corresponding sixteen low-order bits match in five million
  iterations."
  []
  (count (filter identity (take 5000000 (map low-16-bits-match?
                                             (filter #(= 0 (mod % 4)) (build-generator-a))
                                             (filter #(= 0 (mod % 8)) (build-generator-b)))))))
