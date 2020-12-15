(ns advent-of-code-2020.day-15
  "Day 15 solutions.")

(defn initial-state
  "Prime the problem pump by processing the initial sequence of numbers,
  keeping track of when each was last seen, and what the next value
  would be if this was the last starting number."
  [starting-numbers]
  (reduce (fn [acc [i n]]
            (let [turn (inc i)]
              (-> acc
                  (assoc :next (if-let [prev (get (:seen acc) n)] (- turn prev) 0))
                  (update :seen assoc n turn)
                  (assoc :turn turn))))
          {}
          (map-indexed (fn [i n] [i n]) starting-numbers)))

(defn step
  "Take one turn of the game, following the rules in the problem statement."
  [{:keys [turn seen next]}]
  (let [turn (inc turn)]
    {:turn turn
     :seen (assoc seen next turn)
     :next (if-let [prev (get seen next)] (- turn prev) 0)}))

(defn part-1
  "Figure out the number that will be spoken on turn 2020. The initial
  numbers can be supplied as a parameter for testing with sample
  data."
  ([]
   (part-1 [17,1,3,16,19,0]))
  ([starting-numbers]
   (:next (nth (iterate step (initial-state starting-numbers)) (- 2020 1 (count starting-numbers))))))

(defn part-2
  "Figure out the number that will be spoken on turn 30000000. The
  initial numbers can be supplied as a parameter for testing with
  sample data."
  ([]
   (part-2 [17,1,3,16,19,0]))
  ([starting-numbers]
   (:next (nth (iterate step (initial-state starting-numbers)) (- 30000000 1 (count starting-numbers))))))
