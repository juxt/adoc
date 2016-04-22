;; Copyright © 2016, JUXT LTD.

(set-env!
 :source-paths #{"src"}
 :resource-paths #{"src"}

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
      :version +version+}
 jar {:main 'juxt.adoc.core
      :file (str "adoc-" +version+ ".jar")}
 install {:file (str "target/adoc-" +version+ ".jar")
          :pom "juxt/adoc"})

(deftask show-version "Show version" []
  (println (version)))

(deftask dev []
  (comp
   (watch)
   (repl :server true :init-ns 'user)
   (target)))

(deftask install-jar []
  (comp
   (pom)
   (jar)
   (install)))

(deftask deploy
  "Deploy the library. You need to add a repo-map function in your profile.boot that returns the url and credentials as a map.

For example:

{:url \"https://clojars.org/repo/\"
 :username \"billy\"
 :password \"thefish\"}"
  []
  (comp
   (pom)
   (jar)
   (push :repo-map (repo-map "clojars")
         :file (format "target/adoc-%s.jar" +version+)
         :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
