(ns se.raek.lcug.guestbook.model)

(defn file-stored
  "Creates an instance of ref-type with its value loaded from - and on changes
   automatically stored in - the file with the filename."
  [ref-type filename]
  (let [r (ref-type (read-string (slurp filename)))]
    (add-watch r ::file-stored #(spit filename (pr-str %4)))
    r))

(def entries (file-stored ref "data/messages.clj"))

(defn list-entries []
  @entries)

(defn add-entry [{:keys [name text], :as entry}]
  {:pre [(map? entry)
         (string? name)
         (seq name)
         (string? text)
         (seq text)
         (every? #{:name :text} (keys entry))]}
  (alter entries conj entry))
