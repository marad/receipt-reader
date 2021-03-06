(ns lists.reader
  (:refer-clojure :exclude [load-file])
  (:require [clojure.string :as s])
  (:import info.debatty.java.stringsimilarity.Levenshtein))

(def name-regex #"^([A-Za-z0-9,.'/:\-% ]+)\s[AFB]\s")
(def amount-regex #"^.+\s[AFB]\s([0-9A-Z,.]+)\s" )
(def item-price-regex #"^.+\s[AFB]\s.+\s[xX]\s?([0-9A-Z,.]+)\s")
(def price-regex #"\s(-?[0-9A-Z,.]+)[AFB]$")

(defn load-file [path]
  (-> path
      slurp
      s/split-lines))

(defn skip-header [data]
  (let [lev (Levenshtein.)]
    (rest (drop-while #(> (.distance lev "PARAGON FISKALNY" %) 4) data))))

(defn drop-footer [data]
  (take-while #(not (.startsWith % "SPRZEDAZ")) data))

(defn valid-entry? [^String entry]
  (< 6 (count entry)))

(defn read-entry [entry]
  (let [read (fn [regex] (first (rest (re-find regex entry))))
        name (read name-regex)
        amount (read amount-regex)
        item-price (read item-price-regex)
        price (read price-regex)]
    {:parsed {:name name
              :amount amount
              :item-price item-price
              :price price}
     :input entry}))

(defn process [path]
  (->> path
       load-file
       skip-header
       drop-footer
       (filter valid-entry?)
       (map read-entry)))
