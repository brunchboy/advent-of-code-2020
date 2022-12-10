(ns advent-of-code-2022.day-7
  (:require [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.string :as str]))

(def input
  "The instructions (the puzzle input)."
  (->> (io/resource "2022/day_7.txt")
       slurp))

(defn consume-ls
  "Processes all the lines from an ls command until the start of the next
  command, updating the filesystem tree appropriately."
  [lines filesystem cwd]
  (let [[line & rest] lines]
    (if (or (nil? line) (str/starts-with? line "$"))
      [lines filesystem]
      (let [[info filename] (str/split line #"\s+")]
        (if (= info "dir")
          (recur rest (assoc-in filesystem (concat cwd [filename :type]) :directory) cwd)
          (recur rest
                 (assoc-in filesystem (conj cwd filename) {:type :file
                                                           :size (Long/parseLong info)})
                 cwd))))))

(defn parse-input
  "Processes the input lines of the puzzle statement, building the
  representation of the filesystem."
  ([lines]
   (parse-input lines {"/" {:type :directory}} ["/"]))
  ([lines filesystem cwd]
   (if (empty? lines)
     filesystem
     (let [[line & rest]   lines
           [_ command arg] (str/split line #"\s+")]
       (case command
         "cd" (case arg
                "/"  (recur rest filesystem ["/"])
                ".." (recur rest filesystem (pop cwd))
                (recur rest filesystem (conj cwd arg)))
         "ls" (let [[lines filesystem] (consume-ls rest filesystem cwd)]
                (recur lines filesystem cwd)))))))

(defn size
  "Add size information recursively to directories in the filesystem."
  [directory]
  (reduce-kv (fn [m k v]
               (if (keyword? k)
                 (assoc m k v)
                 (let [sized (if (= (:type v) :directory) (size v) v)]
                   (-> m
                       (assoc k sized)
                       (update :size (fnil + 0) (:size sized))))))
             {}
             directory))

(defn sum-small-directories
  "Add up the sizes of all directories whose size is less than or equal
  to 100,000."
  [directory]
  (let [size (:size directory)
        base (if (<= size 100000) size 0)]
    (reduce-kv (fn [total k v]
                 (if (or (keyword? k) (not= (:type v) :directory))
                   total
                   (+ total (sum-small-directories v))))
               base
               directory)))

(defn part-1
  "Solve part 1."
  ([]
   (part-1 input))
  ([data]
   (-> (parse-input (str/split-lines data))
       size
       sum-small-directories)))

(defn smallest-directory-to-remove
  "Find the size of the smallest directory that can be removed that will
  leave enough free space to upgrade the system."
  ([filesystem]
   (let [free   (- 70000000 (:size filesystem))
         needed (- 30000000 free)]
     (smallest-directory-to-remove filesystem needed (:size filesystem))))
  ([directory needed best-found]
   (let [size (:size directory)
         best-found (if (and (>= size needed)
                             (< size best-found))
                      size best-found)]
     (reduce-kv (fn [best k v]
                  (if (or (keyword? k) (not= (:type v) :directory))
                    best
                    (smallest-directory-to-remove v needed best)))
                best-found
                directory))))

(defn part-2
  "Solve part 2."
  ([]
   (part-2 input))
  ([data]
   (-> (parse-input (str/split-lines data))
       size
       smallest-directory-to-remove)))

(def sample-input
  "$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k")
