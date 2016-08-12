(ns juxt.adoc.core-test
  (:require
    [juxt.adoc.core :refer :all]
    [clojure.test :refer :all]))

(deftest coretest
  (testing "extracting content"
    (testing "tables are bad"
      (is (thrown? IllegalArgumentException (doall (get-content "./test/test-folder/tables.adoc")))))
    (testing "all blocks"
      (is (= (count (get-content "./test/test-folder/yada-intro.adoc")) 31)))
    (testing "from multiple files"
      (is (= (count (get-content "./test/test-folder/multiple")) 10)))
    (testing "fetching examples only from multiple"
      (is (= (:content (first (get-source-style "./test/test-folder/multiple"))) "source block")))
    (testing "fetching examples only"
      (is (= (:content (first (get-source-style "./test/test-folder/yada-intro.adoc")))
             "(yada/resource\n  {:properties {…}\n   :methods {:get {:response (fn [ctx] \"Hello World!\")}\n             :put {…}\n             :brew {…}}\n …\n})")))
    ))


