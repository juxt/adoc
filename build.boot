;; Copyright © 2016, JUXT LTD.

(set-env!
 :source-paths #{"src"}
 :dependencies
 '[[camel-snake-kebab "0.3.2"]
   [org.asciidoctor/asciidoctorj "1.5.4"]])

(task-options!
 pom {:project 'juxt/adoc
      :version "0.1.0"}
 jar {:main 'juxt.adoc.core}
 aot {:all true})
