;; Copyright © 2016, JUXT LTD.

(set-env!
 :source-paths #{"src"}
 :resource-paths #{"src"}
 
 :repositories
 #(conj % ["clojars" {:url "https://clojars.org/repo/"}])

 :dependencies
 '[[camel-snake-kebab "0.3.2"]
   [org.asciidoctor/asciidoctorj "1.5.4"]])

(require '[clojure.java.shell :as sh])

(defn next-version [version]
  (when version
    (let [[a b] (next (re-matches #"(.*?)([\d]+)" version))]
      (when (and a b)
        (str a (inc (Long/parseLong b)))))))

(defn version []
  (let [[version commits hash dirty?]
        (next (re-matches #"(.*?)-(.*?)-(.*?)(-dirty)?\n"
                          (:out (sh/sh "git" "describe" "--dirty" "--long" "--tags" "--match" "[0-9].*"))))]
    (if (or dirty? (pos? (Long/parseLong commits)))
      (str (next-version version) "-SNAPSHOT")
      version)))

(def +version+ (version))

(task-options!
 pom {:project 'juxt/adoc
      :version (version)}
 jar {:main 'juxt.adoc.core})

(deftask ver "Get version" []
   (println (version)))

(deftask dev []
  (comp
   (watch)
   (repl :server true :init-ns 'user)
   (target)))

(deftask build []
  (comp
   (pom)
   (jar)
   (target)))

(deftask deploy []
  (comp
   (push :repo "clojars"
         :file (format "target/adoc-%s.jar" +version+))))
