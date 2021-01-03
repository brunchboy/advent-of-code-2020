(ns advent-of-code-2017.day-18
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def input
  "The program instruction lines (puzzle input)."
  (-> "2017/day_18.txt"
      io/resource
      io/reader
      line-seq))

(defn decode
  "Given the current processor `state` and an `arg` value which can
  either be a register name or a number, return the corresponding
  operand value. Numbers are returned directly, register names are
  looked up in the state table and the current value of that register
  is returned."
  [state arg]
  (if (re-matches #"[a-zA-Z]+" arg)
    (get state arg 0)
    (Long/parseLong arg)))

(defn next-instruction
  "Handles the operation of incrementing the program counter.
  Used at the end of each `execute` step (if a jump occurred, the
  program counter will be coming in one lower than the jump target so
  the increment yields the correct final address). If the next
  instruction falls outside of the program, sets the value of `:stop`
  within `state` to `true` to indicate termination."
  [{:keys [pc code] :as state}]
  (let [pc (inc pc)]
    (cond-> (assoc state :pc pc)
      (or (neg? pc) (>= pc (count code)))
      (assoc :stop true))))

(defn execute
  "Given a current processor state, returns what the processor state
  will be after running the next line. `state` is a map whose keys
  include register names (holding the current value of that register),
  `:code` holding the lines of program code, `:pc` holding the address
  of the next instruction to be executed, `:snd` when present holds
  the most recently executed `snd` frequency, and `:rcv` when present
  holds the most recent successful `rcv` frequency. If the next
  instruction fell outside the program due to a jump, `:stop` becomes
  `true`."
  [{:keys [pc code snd] :as state}]
  (let [[op dest arg] (str/split (get code pc) #"\s+")]
    (-> (case op
          "set" (assoc state dest (decode state arg))
          "add" (update state dest (fnil + 0) (decode state arg))
          "mul" (update state dest (fnil * 0) (decode state arg))
          "mod" (update state dest (fnil mod 0) (decode state arg))
          "snd" (assoc state :snd (decode state dest))
          "rcv" (if (zero? (get state dest 0))
                  state
                  (-> state
                      (assoc dest snd)
                      (assoc :rcv snd)))
          "jgz" (cond-> state
                  (pos? (decode state dest))
                  (update :pc + (dec (decode state arg)))))
        next-instruction)))

(defn run
  "Given a list of program lines, sets up the processor state to point
  at the first line and then returns a lazy sequence of the results of
  each processor cycle."
  [lines]
  (iterate execute {:pc 0 :code (vec lines)}))

(defn part-1
  "Solve part 1 of the problem. Run instructions until an `rcv`
  succeeds, and return what it recovered."
  [lines]
  (let [result (first (drop-while #(not (contains? % :rcv)) (run lines)))]
    (:rcv result)))

(defn decode-indexed
  "Given the current processor `state`, an `arg` value which can either
  be a register name or a number, and the current program `id`, return
  the corresponding operand value. Numbers are returned directly,
  register names are looked up in the register table for the specified
  program ID and the current value of that register is returned."
  [state arg id]
  (if (re-matches #"[a-zA-Z]+" arg)
    (get-in state [:registers id arg] 0)
    (Long/parseLong arg)))

(defn next-instruction-indexed
  "Handles the operation of incrementing the program counter for the
  concurrent program identified by `id`. Used at the end of each
  `execute-indexed` step (if a jump occurred, the program counter will
  be coming in one lower than the jump target so the increment yields
  the correct final address). If the next instruction falls outside of
  the program, sets the value of `:stop` within `state` to `true` to
  indicate termination."
  [{:keys [pc code] :as state} id]
  (let [pc (inc (get pc id))]
    (cond-> (assoc-in state [:pc id] pc)
      (or (neg? pc) (>= pc (count code)))
      (assoc-in [:stop id] true))))

(defn execute-indexed
  "Handle the next instruction for one of the programs (identified by
  `id`) in the concurrent processor emulator state (described in more
  depth in `pair-execute` below)."
  [{:keys [pc code stop] :as state} id]
  (if (get stop id)
    state  ; This program has terminated, do no more.
    (let [pc (get pc id)  ; Resolve our own program counter.
          [op dest arg] (str/split (get code pc) #"\s+")]
      (-> (case op
            "set" (assoc-in state [:registers id dest] (decode-indexed state arg id))
            "add" (update-in state [:registers id dest] (fnil + 0) (decode-indexed state arg id))
            "mul" (update-in state [:registers id dest] (fnil * 0) (decode-indexed state arg id))
            "mod" (update-in state [:registers id dest] (fnil mod 0) (decode-indexed state arg id))
            "snd" (-> state
                      (update-in [:buffers (- 1 id)] concat [(decode-indexed state dest id)])
                      (update-in [:send-count id] inc))
            "rcv" (let [buffer (get-in state [:buffers id])]
                    (if (empty? buffer)
                      (assoc-in state [:pc id] (dec pc))  ; Nothing this time, set up to retry.
                      (-> state
                          (assoc-in [:registers id dest] (first buffer))
                          (assoc-in [:buffers id] (rest buffer)))))
            "jgz" (cond-> state
                    (pos? (decode-indexed state dest id))
                    (update-in [:pc id] + (dec (decode-indexed state arg id)))))
          (next-instruction-indexed id)))))

(defn blocked?
  "Given the current processor `state`, checks whether the program with
  the specified `id` is currently trying to read from an empty
  buffer."
  [{:keys [code pc] :as state} id]
  (and (empty? (get-in state [:buffers id]))
       (let [line (get code (get pc id) "")]
         (str/starts-with? line "rcv "))))

(defn deadlocked?
  "Given the current processor `state`, checks for a deadlock situation,
  meaning both programs are trying to receive from empty buffers."
  [state]
  (every? (partial blocked? state) (range 2)))

(defn pair-execute
  "Concurrent upgrade of our processor emulator. Given a current
  processor state, returns what the processor state will be after
  attempting to run the next line for each program. `state` is a map
  whose keys include `:registers` (a tuple holding a pair of maps of
  register names to register values for each program) `:code` holding
  the lines of program code (shared across both programs), `:pc` (a
  tuple holding the address of the next instruction to be executed for
  each program), `:buffers` (a tuple of lists of values sent to each
  program), `:stop` (a tuple indicating whether each program has
  terminated, either by branching outside the code, or if both
  deadlocked reading empty input buffers), and `:send-count` (a tuple
  containing how many times each program has sent a value)."
  [state]
  (if (deadlocked? state)
    (assoc state :stop [true true])
    (-> state
        (execute-indexed 0)
        (execute-indexed 1))))

(defn run-pair
  "Given a list of program lines, sets up the concurrent processor state
  to point at the first line for each program, with empty buffers and
  zero send counts, and registers holding each program's ID in its own
  `p` register, then returns a lazy sequence of the results of each
  processor cycle attempt on both programs."
  [lines]
  (iterate pair-execute {:pc         [0 0]
                         :code       (vec lines)
                         :registers  [{"p" 0} {"p" 1}]
                         :buffers    ['() '()]
                         :stop       [false false]
                         :send-count [0 0]}))

(defn part-2
  "Solve part 1 of the problem. Run instructions until both processors
  stop, and return how many times program 1 sent a value."
  [lines]
  (let [result (first (drop-while #(not (every? identity (:stop %))) (run-pair lines)))]
    (get-in result [:send-count 1])))
