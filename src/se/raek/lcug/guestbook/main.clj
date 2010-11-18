(ns se.raek.lcug.guestbook.main
  (:use [se.raek.lcug.guestbook.controller :only (guestbook-app)]
        [ring.adapter.jetty :only (run-jetty)]))

(def web-server (atom nil))

(defn start []
  (swap! web-server
         #(do (assert (nil? %))
              (run-jetty #'guestbook-app {:port 8080, :join? false}))))

(defn stop []
  (swap! web-server
         #(do (assert %)
              (.stop %)
              nil)))
