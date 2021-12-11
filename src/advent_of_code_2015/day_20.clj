(ns advent-of-code-2015.day-20
  "Solutions for day 20.")

(def input
  "The puzzle input."
  36000000)

(defn part-1
  "Solve part 1."
  []
  (let [houses (atom {})]
    (doseq [elf (range 1 (inc (quot input 10)))]
      (doseq [house (range elf (inc (quot input 10)) elf)]
        (swap! houses update house (fnil + 0) (* elf 10))))
    (first (filter #(>= (get @houses %) input) (range 1 (inc (quot input 10)))))))

(defn part-2
  "Solve part 2."
  []
  (let [houses (atom {})]
    (doseq [elf (range 1 (inc (quot input 10)))]
      (doseq [house (range elf (inc (* elf 50)) elf)]
        (swap! houses update house (fnil + 0) (* elf 11))))
    (first (filter #(>= (get @houses %) input) (range 1 (inc (quot input 10)))))))
