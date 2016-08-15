(ns juxt.adoc.fs
  (:import java.io.File))

(defn find-adoc [path]
  (->> (File. path)
       file-seq
       (filter #(.isFile %))
       (map #(.getPath %))
       (filter #(re-find #"\.adoc" %))
       ))

(defn merge-all [files]
  (let [book-file (java.io.File/createTempFile "book" ".adoc")]
    (with-open [file (clojure.java.io/writer book-file)]
      (binding [*out* file]
        (doseq [f files]
          (print (slurp f)))))
    (.getPath book-file)))

(defn get-adoc [path]
  (if (.isDirectory (File. path))
    (slurp (merge-all (find-adoc path)))
    (slurp path)))
