;; Copyright © 2016, JUXT LTD.

(ns juxt.adoc.core
  (:require
   [camel-snake-kebab.core :refer [->kebab-case-keyword]]
   [clojure.java.io :as io])
  (:import
   (java.util HashMap Collections)
   (org.asciidoctor Asciidoctor$Factory Options Attributes SafeMode)))

(defn engine []
  (Asciidoctor$Factory/create))

(defn document [engine path]
  {:engine engine
   :path path})

(defn ->author [author]
  {:first-name (.getFirstName author)
   :full-name (.getFullName author)
   :initials (.getInitials author)
   :last-name (.getLastName author)
   :middle-name (.getMiddleName author)})

(defn get-header
  "Extract an adoc document header into a map"
  [document]
  (let [m (.readDocumentHeader (:engine document) (io/file (:path document)))]
    {:attributes (reduce-kv (fn [acc k v] (assoc acc (->kebab-case-keyword k) v)) {} (into {} (.getAttributes m)))
     :author (->author (.getAuthor m))
     :authors (map ->author (.getAuthors m))
     :document-title
     (let [t (.getDocumentTitle m)]
       {:main (.getMain t)
        :subtitle (.getSubtitle t)
        :combined (.getCombined t)
        :sanitized? (.isSanitized t)})
     :page-title (.getPageTitle m)
     :revision-info (let [ri (.getRevisionInfo m)]
                      {:date (.getDate ri)
                       :number (.getNumber ri)
                       :remark (.getRemark ri)})}))

(defn get-content [document]
  (.convert (:engine document) (slurp (:path document)) {}))

#_(let [engine (engine)
      doc (document engine "news/20160325-1706.adoc")
      header (get-header doc)
      content (get-content doc)]
  [header content]
  )




