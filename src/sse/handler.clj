(ns sse.handler
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.core.protocols :refer [StreamableResponseBody]]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [clojure.core.async :as a]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]))


(extend-type clojure.core.async.impl.channels.ManyToManyChannel
  StreamableResponseBody
  (write-body-to-stream [channel response output-stream]
    (let [writer (io/writer output-stream)]
      (a/go
        (try
          (loop []
            (when-let [value (a/<! channel)]
              (doto writer (.write value) (.flush))
              (recur)))
          (catch java.io.IOException ex
            (log/error ex))
          (finally
            (a/close! channel)
            (.close output-stream)))))))

(defn stream [channel]
  (-> channel response (content-type "text/event-stream")))

(defn wrap-message [data]
  (str "data:" (json/write-str data) "\n\n"))

(defn stream-handler [request respond raise]
  (let [c (a/chan)]
    (log/info "new sse client")
    (respond (stream c))
    (a/go (a/<! (a/timeout 1000))
          (a/>! c (wrap-message {:timestamp (System/currentTimeMillis)}))
          (a/<! (a/timeout 1000))
          (a/>! c (wrap-message {:timestamp (System/currentTimeMillis)}))
          (a/close! c))))

(defroutes handler
  (GET "/stream" [] stream-handler)
  (route/not-found "<h1>Jog on</h1>"))

(defn index [request]
  (update request :uri #(if (= "/" %) "/index.html" %)))

(defn wrap-index [handler]
  (fn
    ([request]
     (handler
      (index request)))
    ([request respond raise]
     (handler (index request) respond raise))))

(def app
  (-> handler
      wrap-content-type
      (wrap-resource "public")
      wrap-index))
