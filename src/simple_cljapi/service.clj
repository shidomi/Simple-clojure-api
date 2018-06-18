(ns simple-cljapi.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [clojure.data.json :as json]
            [ring.util.response :as ring-resp]
            [monger.core :as mg]
            [monger.collection :as mc]
            [monger.conversion :refer [from-db-object]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]))

(defn insert-data
  [data]
  (let [conn (mg/connect)
      db   (mg/get-db conn "cljsample")
      coll "vehicles"]
  (mc/insert db coll {:brand (get data :brand)
                      :model (get data :model)
                      :color (get data :color)})))

(defn receive-data
  [param]
  (let [conn (mg/connect)
      db   (mg/get-db conn "cljsample")
      coll "vehicles"]
  (mc/find-maps db coll {:color param})))

(defn get-vehicles
  [param]
  (def result {})
  (for [item (receive-data param)]
    (conj result (dissoc item :_id))))

(defn vehicles-list
  [request]
  (http/json-response (get-vehicles (get request :query-string))))

(defn vehicles-create
  [request]
  (insert-data (get request :json-params))
  (ring-resp/response ""))

(def common-interceptors [(body-params/body-params) http/html-body])

(def routes #{["/vehicles" :get (conj common-interceptors `vehicles-list)]
              ["/vehicles" :post (conj common-interceptors `vehicles-create)]})

(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/host "localhost"
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
