(ns advent-of-code-2017.day-10)

(defn looped-reverse
  "Performs the basic knotting operation described in the problem
  statement. Given a list of values, a position, and a length,
  reverses the sub-list of values which starts at that position and
  has that length, considering the list to be circular in cases when
  the operation would run off the end."
  [vals pos len]
  (let [shifted   (->> vals
                       cycle  ; Make the list circular
                       (drop pos)  ; Move to the specified position.
                       (take (count vals)))  ; And truncate to the actual length again.
        reversed  (->> shifted  ; Build the reversed section of the proper length.
                       (take len)
                       (reverse))
        remaining (drop len shifted)]  ; Extract the rest of the list which was not reversed.
    (->> (concat reversed remaining)  ; Put them back together.
         cycle  ; Make them circular again.
         (drop (- (count vals) pos))  ; Move us back to where the list is supposed to start.
         (take (count vals)))))  ; And truncate to the actual length again.


(defn run-round
  "Runs a single round of the Knot Hash algorithm. This was extracted
  from the body of `part-1` once it became clear that it needed to be
  done multiple times for part 2. Given a starting list of values,
  list of lengths, current position, and skip size, reverses sections
  of the value list of lengths specified in the lengths list,
  advancing by length + skip size and incrementing skip size after
  each reversal. Returns a tuple with the same structure that was
  received as input, but containing the transformed list and the
  updated position and skip count, so that repeated rounds can be
  easily iterated."
  [[vals lengths pos skip]]
  (loop [vals         vals
         pos          pos
         skip         skip
         [len & left] lengths]
     (if len
       (recur (looped-reverse vals pos len)
              (mod (+ pos len skip) (count vals))  ; The mod isn't strictly needed but helps performance.
              (inc skip)
              left)
       [vals lengths pos skip])))

(defn part-1
  "Solve part 1 of the problem. Run a single round of the knot hash and
  return the product of the first two values in the resulting value
  list. The starting values and lengths list can be supplied as
  arguments in order to unit test using the sample problem; otherwise
  the puzzle input values are used."
  ([]
   (part-1 (range 256) [106,16,254,226,55,2,1,166,177,247,93,0,255,228,60,36]))
  ([vals lengths]
   (let [[vals] (run-round [vals lengths 0 0])]
     (* (first vals) (second vals)))))  ; Return the product of the first two resulting values.

(defn build-lengths
  "Converts an input string to its ASCII values, then appends the
  standard suffix sequence specified in the part 2 problem statement."
  [input]
  (concat (map int input) [17, 31, 73, 47, 23]))

(defn part-2
  "Solve part 2 of the problem. Convert the input string to a list of
  lengths, run 64 rounds of the knot hash algorithm, then condense the
  sparse hash to a dense hash and format it as a hexadecimal value.
  The input string can be supplied as an argument to unit test using
  the sample problems, otherwise the puzzle input is used."
  ([]
   (part-2 "106,16,254,226,55,2,1,166,177,247,93,0,255,228,60,36"))
  ([input]
   (let [lengths (build-lengths input)
         [sparse] (nth (iterate run-round [(range 256) lengths 0 0]) 64)]
     (->> sparse
          (partition 16)
          (map #(apply bit-xor %))
          (map #(format "%02x" %))
          (apply str)))))
