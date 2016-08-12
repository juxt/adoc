;; Copyright © 2016, JUXT LTD.

(ns juxt.adoc.core
  (:require
   [camel-snake-kebab.core :refer [->kebab-case-keyword]]
   [juxt.adoc.fs :as fs]
   [clojure.java.io :as io])
  (:import
   (java.util HashMap Collections)
   java.io.File
   (org.asciidoctor Asciidoctor$Factory Options Attributes SafeMode)
   (org.asciidoctor.ast Document SectionImpl BlockImpl)))

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

(defn keywordize [m]
  (reduce-kv
    (fn [acc k v]
      (assoc acc (if (instance? clojure.lang.Named k) (->kebab-case-keyword k) k) v)) {} m))

(defn ->attributes [block]
  (when (instance? SectionImpl block)
    (keywordize (into {} (.getAttributes block)))))

(defn get-header
  "Extract an adoc document header into a map"
  [document]
  (let [m (.readDocumentHeader (:engine document) (io/file (:path document)))]
    {:attributes (->attributes m)
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

(defn get-content-as-html [document]
  (.convert (:engine document) (slurp (:path document)) {}))

(defn block-seq
  "Return all the blocks in a depth-first search of the given document"
  [doc]
  (tree-seq
    (fn branch? [node]
      (or
       (instance? org.asciidoctor.ast.Document node)
       (instance? org.asciidoctor.ast.BlockImpl node)
       (instance? org.asciidoctor.ast.SectionImpl node)))
    (fn children [node] (.getBlocks node))
    doc))

(defn get-content
  "Parse the asciidoc content of the file at path, returning
  all the blocks. Optional filtering with predicate p."
  [path & [p]]
  (let [doc (.load (engine) (fs/get-adoc path) (java.util.HashMap.))
        p (or p identity)]
    (filter p (->> (block-seq doc)
                   (map bean)
                   (map #(update-in % [:attributes] (fn [a] (keywordize (into {} a)))))
                   ))))

(defn get-source-style
  "Extract the source blocks from the adoc file at path."
  [path]
  (get-content path #(= "source" (:style %))))
