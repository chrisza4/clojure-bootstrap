(ns rest-crud-demo.core
  (:require
    [rest-crud-demo.user :refer [user-routes]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.reload :refer [wrap-reload]]
    [toucan.db :as db]
    [toucan.models :as models]
    [rest-crud-demo.auth :refer [auth-routes]]
    [rest-crud-demo.hello :refer [hello-routes]]
    [compojure.api.sweet :refer [api routes]])
  (:gen-class))

(def db-spec
  {:dbtype   "postgres"
   :dbname   "postgres"
   :user     "postgres"
   :password "abc123ABC"})

(def swagger-config
  {:ui      "/swagger"
   :spec    "/swagger.json"
   :options {:ui   {:validatorUrl nil}
             :data {:info {:version "1.0.0", :title "Restful CRUD API"}}}
   :data {:securityDefinitions {:api_key {:type "apiKey" :name "Authorization" :in "header"}}}})

(def app
  (do
    (db/set-default-db-connection! db-spec)
    (models/set-root-namespace! 'rest-crud-demo.models)
    (api
      {:swagger swagger-config}
      (apply routes auth-routes user-routes hello-routes))))

(defn -main
  [& args]
  (run-jetty 
    (wrap-reload #'app) 
    {:port 3000}))
