(ns image
  (:require [clojure.java.io :as io]
            [clojure.string :as string])
  (:import javax.imageio.ImageIO)
  (:import deskew.deskew)
  (:import ImageOperations)
  (:import java.awt.geom.AffineTransform)
  (:import java.awt.image.AffineTransformOp)
  (:import java.awt.image.BufferedImage)
  (:import java.awt.Color))

(defn load-image [path]
  (let [file (io/file path)]
    (ImageIO/read file)))

(defn- get-ext [fname]
  (->> fname
       reverse
       (take-while #(not (= % \.)))
       reverse
       string/join))

(defn save-image [image path]
  (let [file (io/file path)]
    (ImageIO/write image (get-ext path) file)))

(defn deskew-angle [image]
  (deskew/doIt image))

(defn threshold [image value]
  (ImageOperations/threshold image value))

(defn draw-on-white [image]
  (let [w (.getWidth image)
        h (.getHeight image)
        new-image (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
        g (.createGraphics new-image)]
    (.drawImage g image 0 0 Color/white nil)
    new-image))

(defn rotate-image [image radians]
  (let [transform (doto (AffineTransform.)
                    (.rotate radians
                             (/ (.getWidth image) 2)
                             (/ (.getHeight image) 2)))
        op (AffineTransformOp. transform AffineTransformOp/TYPE_BICUBIC)]
    (.filter op image nil)))

(defn deskew-image [image]
  (rotate-image image (deskew-angle image)))

(defn process [input-path output-path]
  (-> input-path
       load-image
       (threshold 160)
       deskew-image
       draw-on-white
       (save-image output-path)))
