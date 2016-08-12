(defproject juxt/adoc "0.1.7"
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
