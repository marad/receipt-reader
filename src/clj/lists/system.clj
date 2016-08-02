(ns lists.system
  (:require [mount.core :as mount]
            sketch))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

(defn reset []
  (stop)
  (start))


