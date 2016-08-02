(ns lists.core
  (:require [lists.image :as i]
            [lists.reader :as r]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as s])
  (:import java.io.File)
  (:gen-class))

(defn remove-ext [path]
  (->> path
       reverse
       (drop-while #(not (= \. %)))
       rest
       reverse
       s/join))

(defn tesseract [input-path output-path]
  (let [result (sh "tesseract" input-path (remove-ext output-path) "-l" "rec")]
    (if (not (= 0 (:exit result)))
      (do (binding [*out* *err*]
                   (println (:err result)))))
    result))

(defn temp-file [prefix suffix]
  (.getAbsolutePath (doto (File/createTempFile prefix suffix)
                      (.deleteOnExit))))

(defn process-image [image-path]
  (let [corrected-image-path (temp-file "receipt" ".png")
        scanned-path (temp-file "receipt" ".txt")]
    (println "Preparing image...")
    (i/process image-path corrected-image-path)
    (println "Scanning image...")
    (tesseract corrected-image-path scanned-path)
    (println "Processing scanned list...")
    (r/process scanned-path)))

(defn -main [& args]
  (let [image-path (first args)
        out-path (second args)
        data (process-image image-path)]
    (println "Writing output file" out-path "...")
    (spit "test.edn" (with-out-str (pr data)))
    (System/exit 0)))
