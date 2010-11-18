(ns se.raek.lcug.guestbook.view
  (:use net.cgrand.enlive-html))

(deftemplate guestbook-template "templates/guestbook.html"
  [entries]
  [:.no-entries] (fn [match]
                   (when (empty? entries)
                     match))
  [:.entry]      (clone-for [{:keys [name text]} entries]
                   [:.name] (content name)
                   [:.text] (content text)))

(deftemplate error-template "templates/error.html"
  [code name message]
  [:title] (content (str code \space name))
  [:h1]    (content (str code \space name))
  [:p]     (content (str message)))
