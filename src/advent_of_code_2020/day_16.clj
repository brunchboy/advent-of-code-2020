(ns advent-of-code-2020.day-16
  "Solutions for day 16."
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The ticket notes (puzzle input)."
  (->> (io/resource "day_16.txt")
       io/reader
       line-seq))

(defn sections
  "Returns a sequence of each meaningful section of the notes, i.e.
  split at the blank lines."
  [coll]
  (->> coll
       (partition-by str/blank?)
       (take-nth 2)))

(defn parse-rules
  "Given the section of the problem input which defines field titles and
  range rules, builds a map whose keys are the field titles and whose
  values are a list of tuples containing the lower and upper bounds
  for all the valid value ranges for that field."
  [rules]
  (reduce (fn [acc rule]
            (let [[title ranges] (str/split rule #":\s*")
                  ranges         (str/split ranges #"\s+or\s+")
                  ranges         (map (fn [range]
                                        (let [bounds (str/split range #"\s*-\s*")]
                                          (map #(Long/parseLong %) bounds)))
                                      ranges)]
              (assoc acc title ranges)))
          {}
          rules))

(defn parse-notes
  "Given the problem input, builds a map containing the field
  rules (names and valid value ranges), a vector of the field values
  of my ticket, and a list of nearby tickets (each of which is its own
  vector of field values)."
  ([]
   (parse-notes input))
  ([notes]
   (let [[rules mine nearby] (sections notes)]
     {:rules (parse-rules rules)
      :mine  (->> (str/split (second mine) #",")
                  (mapv #(Long/parseLong %)))
      :nearby (map (fn [ticket]
                     (mapv #(Long/parseLong %) (str/split ticket #",")))
                   (rest nearby))})))

(defn valid-for-ranges?
  "Given a list of valid ranges, checks whether the supplied value falls
  within any of them."
  [ranges val]
  (some (fn [[low high]]
          (<= low val high))
        ranges))

(defn valid-for-any-field?
  "Given a candidate value and the list of field rules, checks whether
  the value is valid for any field."
  [rules val]
  (some (fn [ranges]
          (valid-for-ranges? ranges val))
        (vals rules)))

(defn part-1
  "Parses the puzzle input, scans all the field values on nearby
  tickets, filters out any which are valid by any field rule, and sums
  the ones which were not."
  ([]
   (part-1 input))
  ([notes]
   (let [{:keys [rules nearby]} (parse-notes notes)]
     (->> nearby
          flatten
          (remove (partial valid-for-any-field? rules))
          (apply +)))))

(defn remove-invalid-tickets
  "Given a list of field rules and potential tickets, removes any
  tickets which contain values that are not valid for any field."
  [rules tickets]
  (filter (fn [ticket]
            (every? (partial valid-for-any-field? rules) ticket))
          tickets))

(defn valid-fields
  "Given a list of range rules, and a list of values which were found in
  each field position on all the nearby tickets, returns a set of all
  the field indices for which every value was valid."
  [ranges fields]
  (reduce (fn [acc [i field]]
            (if (every? #(valid-for-ranges? ranges %) field)
              (conj acc i)
              acc))
          #{}
          (partition 2 (interleave (range) fields))))

(defn find-field-candidates
  "Determine which field indices are valid for each named field. Builds
  a map whose keys are the field names defined in the problem input,
  and whose values are the set of field indices for which every ticket
  has a valid value for that field."
  [rules nearby]
  (let [fields (apply map vector nearby)]
    (reduce-kv (fn [acc title ranges]
                 (assoc acc title (valid-fields ranges fields)))
               {}
               rules)))

(defn remove-elsewhere-if-singleton
  "Given a field name and a map of field candidates (as described in
  `find-field-candidates`), checks whether the field is valid for only
  one ticket position. If that is true, then updates the candidate map
  to remove that position from every other field."
  [title candidates]
  (let [current (get candidates title)]
    (if (= (count current) 1)
      (reduce-kv (fn [acc k v]
                   (if (= k title)
                     (assoc acc k v)
                     (assoc acc k (set/difference v current))))
                    {}
                    candidates)
      candidates)))

(defn prioritize-singletons
  "Given a map of field candidates (as described in
  `find-field-candidates`), resolves ambiguity by scanning for any
  fields that are only valid for a single position, and removing that
  position from other fields."
  [candidates]
  (let [narrowed (loop [fields candidates
                        titles (keys candidates)]
                   (if-let [current (first titles)]
                     (recur (remove-elsewhere-if-singleton current fields)
                            (rest titles))
                     fields))]
    (if (= narrowed candidates)
      (throw (Exception. (str "Cannot narrow further: " candidates)))
      narrowed)))

(defn resolve-candidates
  "Given a map of field candidates (as described in
  `find-field-candidates`), resolves ambiguity by repeatedly calling
  `prioritize-singletons` until there are no more fields that could be
  in multiple positions (success), or there is some field that can't
  be in any position (failure)."
  [candidates]
  (loop [fields candidates]
       (cond
         (some empty? (vals fields))
         (throw (Exception. (str "Some field has no candidate location:" fields)))

         (every? #(= 1 (count %)) (vals fields))
         fields

         :else (recur (prioritize-singletons fields)))))

(defn part-2
  "Solves part 2 parsing the problem input, removing any invalid
  tickets, figuring out which fields must be in which positions, then
  multiplying the values of the fields whose name begines with the
  word departure in our own ticket."
  ([]
   (part-2 input))
  ([notes]
   (let [{:keys [rules nearby mine]} (parse-notes notes)
         nearby                      (remove-invalid-tickets rules nearby)
         candidates                  (find-field-candidates rules nearby)
         fields                      (resolve-candidates candidates)]
     (reduce-kv (fn [acc title v]
                  (if (str/starts-with? title "departure ")
                    (* acc (nth mine (first v)))
                    acc))
                1
                fields))))
