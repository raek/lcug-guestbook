(ns se.raek.lcug.guestbook.controller
  (:require [clojure.string :as str])
  (:use (se.raek.lcug.guestbook [model :only (list-entries add-entry)]
                                [view :only (guestbook-template error-template)])
        [net.cgrand.moustache :only (app pass)]
        [ring.handler.dump :only (handle-dump)]
        (ring.middleware [params :only (wrap-params)]
                         [file :only (wrap-file)]
                         [file-info :only (wrap-file-info)]
                         [stacktrace :only (wrap-stacktrace)])))

(defn success [body]
  {:status 200
   :headers {"Content-Type" "text/html; charset=UTF-8"}
   :body body})

(def error-names
  {404 "Not Found"
   422 "Unprocessable Entity"})

(defn redirect [uri]
  {:status 302
   :headers {"Content-Type" "text/plain; charset=UTF-8"
             "Location" uri}
   :body (str "Redireting to " uri)})

(defn error [code & strs]
  {:status code
   :headers {"Content-Type" "text/html; charset=UTF-8"}
   :body (error-template code (error-names code) (apply str strs))})

(defn wrap-required-fields [handler field-set]
  (fn [{:keys [form-params], :as request}]
    (if-let [unknown-fields (seq (remove field-set (keys form-params)))]
      (error 422 "unknown fields: " unknown-fields)
      (loop [fields (seq form-params)]
        (if-not fields
          (handler request)
          (if (str/blank? (val (first fields)))
            (error 422 "missing field: " (name (key (first fields))))
            (recur (next fields))))))))

(defn get-guestbook [request]
  (->> (list-entries)
       (take 10)
       (guestbook-template)
       (success)))

(defn post-guestbook [{{:strs [name text captcha]} :form-params, :as request}]
  (if-not (= captcha "hest")
    (error 422 "captcha failed: expected \"hest\", got \"" captcha "\"")
    (dosync
      (add-entry {:name name, :text text})
      (get-guestbook request))))

(defn wrap-throwable
  "Middleware for making wrap-stacktrace catch all Throwables."
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (throw e))
      (catch Throwable t
        (throw (Exception. t))))))

(def guestbook-app
  (app wrap-stacktrace
       wrap-throwable
       wrap-params
       wrap-file-info
       []           {:get  get-guestbook
                     :post [(wrap-required-fields #{"name" "text" "captcha"})
                            post-guestbook]}
       ["static" &] {:get  [(wrap-file "resources/static/")
                            pass]}
       [&]          {:any  (fn [_] (error 404 "The resource could not be found"))}))

