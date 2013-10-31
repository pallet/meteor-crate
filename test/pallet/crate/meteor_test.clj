(ns pallet.crate.meteor-test
  (:require
   [clojure.test :refer :all]
   [clojure.tools.logging :as logging]
   [pallet.build-actions :as build-actions :refer [build-actions]]
   [pallet.crate.meteor :as meteor]))

(deftest invoke-test
  (is (build-actions {}
        (meteor/settings {})
        (meteor/install {}))))
