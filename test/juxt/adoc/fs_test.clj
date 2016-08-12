(ns juxt.adoc.fs-test
  (:require
    [juxt.adoc.fs :refer :all]
    [clojure.test :refer :all]))

(deftest fstest
  (testing "searching for files in the file system"
    (testing "fetching all adoc recursively starting at path"
      (is (= (find-adoc "./test/test-folder")
             ["./test/test-folder/multiple/1.adoc"
              "./test/test-folder/multiple/2.adoc"
              "./test/test-folder/recursive/alsothis.adoc"
              "./test/test-folder/tables.adoc"
              "./test/test-folder/yada-intro.adoc"
              "./test/test-folder/yep.adoc"])))))
