(ns lists.gui
  (:require [mount.core :refer [defstate] :as mount]
            [seesaw.core :refer [frame config! pack! show!]]))

(defn create-window []
  (frame :title "Hello World"
         :content "This is sparta!"))

(defstate ^{:on-reload :noop} window :start (create-window))

(defn show [content]
  (config! window :content content)
  (-> window pack! show!))
