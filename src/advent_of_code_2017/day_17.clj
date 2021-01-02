(ns advent-of-code-2017.day-17)

(def input
  "The steps to move at each round of the spinlock algorithm (puzzle
  input)."
  354)

(defn build-stepper
  "Builds a function that transforms the circular buffer by a single
  step according to the problem statement rules. The argument to
  `build-stepper` is the number of moves advanced before insertion at
  each step. Inputs to the stepper function are a tuple of the current
  buffer contents (represented by a simple list), and the current
  position within that buffer. Outputs are the same tuple reflecting
  the motion and insertion that occured during this step."
  [steps]
  (fn [[buffer pos]]
    (let [pos (inc (mod (+ pos steps) (count buffer)))
          buffer (concat (take pos buffer) [(count buffer)] (drop pos buffer))]
      [buffer pos])))

(defn part-1
  "Solve part 1 of the problem. Build a stepper with the specified steps
  per round, iterate it 2017 times, and return the value after element
  2017."
  [steps]
  (let [[buffer pos] (nth (iterate (build-stepper steps) [[0] 0]) 2017)]
    (nth buffer (mod (inc pos) (count buffer)))))

(defn part-2
  "Solve part 2 of the problem. This was clearly not going to work
  brute-force, so I had to think about what determines when we get a
  new value after zero. That turns out to be only whenever our
  position is zero, so we can ignore the rest of the list."
  [steps final-value]
  (loop [val   1
         pos   0
         after nil]
    (if (= val final-value)
      after
      (let [pos (mod (+ pos steps) val)]
        (recur (inc val)
               (inc pos)
               (if (zero? pos) val after))))))
