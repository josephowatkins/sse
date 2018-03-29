(ns sse.config
  (:require [environ.core :refer [env]]))


(def port
  (Integer/parseInt (env :port "3000")))

(def environment
  (env :environment "development"))
