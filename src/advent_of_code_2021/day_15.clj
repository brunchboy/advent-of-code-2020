(ns advent-of-code-2021.day-15
  "Solutions for day 15."
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.data.priority-map :as prio]))

(defn node-name
  [x y]
  (keyword (str "n" x "-" y)))

(defn neighbor
  [lines x y]
  (when-let [c (get-in lines [y x] nil)]
    (let [cost (Long/parseLong (str c))]
      [(node-name x y) cost])))

(defn neighbors
  [lines x y]
  (into {} (filter identity
                   [(neighbor lines (dec x) y)
                    (neighbor lines (inc x) y)
                    (neighbor lines x (dec y))
                    (neighbor lines x (inc y))])))

(defn build-graph
  [text]
  (into {}
        (let [lines (str/split-lines text)]
          (for [y (range (count lines))
                x (range (count (first lines)))]
            [(node-name x y) (neighbors lines x y)]))))

(def input
  (-> (io/resource "2021/day_15.txt")
      slurp
      build-graph))

;;; Clojure Dijkstra implementation based on
;;; https://gist.github.com/rbuchmann/82d5e202845ce43f273bbe21b06674f3

(def all-nodes (comp set flatten (juxt keys #(map keys (vals %)))))

(defn dijkstra-step [graph to-visit]
  (let [[current {:keys [path distance]}] (peek to-visit)
        update-neighbours (fn [distances [node edge-length]]
                            (update distances node
                                    (comp first #(sort-by :distance [%1 %2]))
                                    {:distance (+ distance edge-length)
                                     :path (conj (or path []) current)}))
        new-distances (reduce update-neighbours
                              to-visit
                              (select-keys (graph current) (keys to-visit)))]
    (dissoc new-distances current)))

(defn shortest-path [graph start end]
  (->> (-> (into (prio/priority-map-keyfn :distance)
                 (map vector (all-nodes graph) (repeat {:distance Double/POSITIVE_INFINITY})))
           (assoc start {:distance 0}))
       (iterate (partial dijkstra-step graph))
       (some (fn [to-visit]
               (when (or (= end (-> to-visit peek first))
                         (every? #(= Double/POSITIVE_INFINITY (:distance %)) (vals to-visit)))
                 (let [end-node (to-visit end)]
                   [(when-let [path (:path end-node)]
                      (conj path end))
                    (or (:distance end-node) Double/POSITIVE_INFINITY)]))))))

;;; End of Dijkstra implementation

(defn part-1
  ([]
   (part-1 input 100 100))
  ([graph w h]
   (second (shortest-path graph (node-name 0 0) (node-name (dec w) (dec h))))))


(defn neighbor-2
  [lines x y w h]
  (when (and  (< -1 y (* 5 h)) (< -1 x (* 5 w)))
    (let [c    (get-in lines [(mod y h) (mod x w)])
          cost (Long/parseLong (str c))]
      [(node-name x y) (inc (mod (dec (+ cost (quot x w) (quot y h))) 9))])))

(defn neighbors-2
  [lines x y w h]
  (into {} (filter identity
                   [(neighbor-2 lines (dec x) y w h)
                    (neighbor-2 lines (inc x) y w h)
                    (neighbor-2 lines x (dec y) w h)
                    (neighbor-2 lines x (inc y) w h)])))

(defn build-graph-2
  [text]
  (into {}
        (let [lines (str/split-lines text)
              w     (count (first lines))
              h     (count lines)]
          (for [y (range (* 5 h))
                x (range (* 5 w))]
            [(node-name x y) (neighbors-2 lines x y w h)]))))

(def input-2
  (-> (io/resource "2021/day_15.txt")
      slurp
      build-graph-2))

(defn part-2
  ([]
   (part-2 input-2 500 500))
  ([graph w h]
   (second (shortest-path graph (node-name 0 0) (node-name (dec w) (dec h))))))
