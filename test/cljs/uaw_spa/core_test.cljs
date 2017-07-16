(ns uaw-spa.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [uaw-spa.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
