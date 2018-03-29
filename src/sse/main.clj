(ns sse.main
  (:require [clojure.tools.logging :as log]
            [sse.handler :refer [app]]
            [sse.config :as config]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))


(defn -main [& args]
  (log/infof "starting server on port %s" config/port)
  (run-jetty app {:port config/port :join false :async? true}))
