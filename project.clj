(defproject sse "SNAPSHOT"
  :description "Server Sent Events"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/core.async "0.4.474"]
                 [ring "1.6.3"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [org.clojure/data.json "0.2.6"]]

  :plugins [[lein-ring "0.12.4"]]

  :ring {:handler sse.handler/app
         :async? true}

  :main ^:skip-aot sse.main

  :profiles {:uberjar {:aot [sse.main]
                       :uberjar-name "app.jar"}

             :dev {:plugins [[lein-environ "1.1.0"]]
                   :env {:port "3000"
                         :environment "development"}}})
