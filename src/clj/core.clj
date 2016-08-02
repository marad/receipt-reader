(ns core
  (:require [image :as i]
            [reader :as r]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as s])
  (:import java.io.File))

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

(defn -main []
  (println "Receipt image path: ")
  (let [data (process-image (read-line))]
    (println data))
  )


; image -code-> processed image -tesseract-> text file -code-> csv
