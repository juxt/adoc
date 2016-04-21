;; Copyright © 2016, JUXT LTD.

(ns juxt.adoc.core
  (:require
   [camel-snake-kebab.core :refer [->kebab-case-keyword]]
   [clojure.java.io :as io])
  (:import
   (java.util HashMap Collections)
   (org.asciidoctor Asciidoctor$Factory Options Attributes SafeMode)
   (org.asciidoctor.ast Document SectionImpl)))

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


(defn block-seq
  "Return all the blocks in a depth-first search of the given document"
  [doc]
  (tree-seq
    (fn branch? [node]
      (or
       (instance? org.asciidoctor.ast.Document node)
       (instance? org.asciidoctor.ast.SectionImpl node)))
    (fn children [node] (.getBlocks node))
    doc))
