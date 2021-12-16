(ns advent-of-code-2021.day-16
  "Solutions for day 16."
  (:require [clojure.java.io :as io]))

(def input
  "The puzzle input."
  (-> (io/resource "2021/day_16.txt")
      slurp))

(defn hex->binary
  "Converts a hex string to the corresponding binary string."
  [s]
  (->> s
       (partition 2)
       (map #(apply str %))
       (map #(Long/parseLong % 16))
       (map #(Long/toBinaryString (+ 256 %)))
       (map #(subs % 1))
       (apply str)))

(defn parse-literal
  "Parses a literal packet according to the rules of the problem.
  Returns the parsed structure and the index of the next character
  beyond the packet."
  [s]
  (loop [i      6
         result 0]
    (let [more?  (= (.charAt s i) \1)
          chunk  (Long/parseLong s (inc i) (+ i 5) 2)
          result (+ (* result 16) chunk)
          i      (+ i 5)]
      (if more?
        (recur i result)
        [{:literal result} i]))))

(declare parse-packet)

(defn parse-sub-packets-by-count
  "Parses a series of packets given the number of packets. Returns a
  vector of the parsed structures, and the index of the next character
  beyond the final packet."
  [s n]
  (loop [result []
         i      0
         pos    0]
    (if (< i n)
      (let [[packet end] (parse-packet (subs s pos))]
        (recur (conj result packet)
               (inc i)
               (+ pos end)))
      [result pos])))

(defn parse-sub-packets-by-length
  "Parses a series of packets given the number of bits to consume.
  Returns a vector of the parsed structures, and the index of the next
  character beyond the final packet, which will equal `l`."
  [s l]
  (loop [result []
         pos    0]
    (if (< pos l)
      (let [[packet end] (parse-packet (subs s pos))]
        (recur (conj result packet)
               (+ pos end)))
      [result pos])))

(defn parse-operator
  "Parses an operator packet, according to the rules of the problem,
  including its sub-packets. Returns the hierarchy of parsed packets,
  and the index of the next character beyond the final packet."
  [s operator]
  (if (= (.charAt s 6) \1)
    (let [n                 (Long/parseLong s 7 18 2)
          [sub-packets end] (parse-sub-packets-by-count (subs s 18) n)]
      [{:operator operator
        :sub-packets      sub-packets} (+ end 18)])
    (let [l                 (Long/parseLong s 7 22 2)
          [sub-packets end] (parse-sub-packets-by-length (subs s 22) l)]
      [{:operator    operator
        :sub-packets sub-packets} (+ end 22)])))

(defn parse-packet-content
  "Parses a packet beyond the version and type header. Returns the
  parsed structure, and the index of the next character beyond the
  parsed packet."
  [s kind]
  (case kind
    0 (parse-operator s :sum)
    1 (parse-operator s :product)
    2 (parse-operator s :minimum)
    3 (parse-operator s :maximum)
    4 (parse-literal s)
    5 (parse-operator s :greater-than)
    6 (parse-operator s :less-than)
    7 (parse-operator s :equal)))

(defn parse-packet
  "Parses a full packet following the rules of the problem statement.
  Returns a tuple of the parsed structure and the index of the next
  character beyond the end of the packet."
  [s]
  (let [version       (Long/parseLong s 0 3 2)
        kind          (Long/parseLong s 3 6 2)
        [content end] (parse-packet-content s kind)]
    [(merge {:version version
             :type   kind}
            content)
     end]))

(defn sum-versions
  "Calculates the sum of all the versions found in a packet and its
  nested sub-packets."
  [packet]
  (+ (:version packet) (apply + (map sum-versions (:sub-packets packet)))))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([hex]
   (let [[packet] (parse-packet (hex->binary hex))]
     (sum-versions packet))))

(defn evaluate
  "Evaluates the expression represented by a packet, returning the
  result."
  [packet]
  (if-let [val (:literal packet)]
    val
    (let [args (map evaluate (:sub-packets packet))]
      (case (:operator packet)
        :sum          (apply + args)
        :product      (apply * args)
        :minimum      (apply min args)
        :maximum      (apply max args)
        :greater-than (let [[left right] args]
                        (if (> left right) 1 0))
        :less-than    (let [[left right] args]
                        (if (< left right) 1 0))
        :equal        (let [[left right] args]
                        (if (= left right) 1 0))))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([hex]
   (let [[packet] (parse-packet (hex->binary hex))]
     (evaluate packet))))
