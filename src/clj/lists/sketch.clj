(ns lists.sketch
  (:require [mount.core :refer [defstate] :as mount]
            [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (let [receipt (q/load-image "intermarche-2016-07-30.jpg")]
    (.resize receipt 300 300)
    (q/background 0)
    (q/frame-rate 10)
    (q/smooth)
    (q/text-size 20)
    {:receipt receipt
     :text "Hello"}))

(defn draw [state]
  (q/background 255)
  (q/stroke 0)
  (q/stroke-weight 10)
  (q/fill 100)
  (q/image (:receipt state) 0 0)
  (q/point 10 10)
  (.loadPixels (:receipt state))
  (q/text (str "Info " (get (.pixels (:receipt state)) 2)) 160 20))

(defn update-state [state] state)

(defn create-sketch []
  (q/defsketch fun
    :host "host"
    :size [300 300]
    :setup setup
    :draw draw
    :update update-state
    :middleware [m/fun-mode]))

(defstate ^{:on-reload :noop} sketch
  :start (create-sketch))
