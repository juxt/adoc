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

(defproject juxt/adoc +version+
  :description "Clojure wrapper around AsciidoctorJ"
  :url "https://github.com/juxt/adoc"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [camel-snake-kebab "0.3.2"]
                 [org.asciidoctor/asciidoctorj "1.5.4"]]
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.16.0"]]}}
  :test-refresh {:quiet true})
