;;; Pallet project configuration file

(require
 '[pallet.crate.meteor-test :refer [test-spec]]
 '[pallet.crates.test-nodes :refer [node-specs]])

(defproject meteor-crate
  :provider node-specs                  ; supported pallet nodes
  :groups [(group-spec "meteor-live-test"
             :extends [with-automated-admin-user
                       test-spec]
             :roles #{:live-test :default})])
