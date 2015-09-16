(ns pallet.crate.meteor
  "Install and configure meteor"
  (:require
   [clojure.java.io :refer [file]]
   [clojure.string :as string]
   [clojure.tools.logging :refer [debugf]]
   [pallet.action :refer [with-action-options]]
   [pallet.actions
    :refer [exec-checked-script packages remote-directory remote-file]]
   [pallet.common.context :refer [throw-map]]
   [pallet.api :as api :refer [plan-fn]]
   [pallet.crate :refer [assoc-settings defmethod-plan defplan get-settings]]
   [pallet.crate-install :as crate-install]
   [pallet.script.lib :refer [rm]]
   [pallet.utils :refer [apply-map deep-merge maybe-assoc]]
   [pallet.version-dispatch
    :refer [defmethod-version-plan defmulti-version-plan]]
   [pallet.versions :refer [version-string as-version-vector]]))

(def facility :meteor)

(def ^:dynamic *meteor-defaults*
  {:version "0.6.6.2"})

;;; # Build helpers

(def ^:dynamic *bootstrap-url*
  "https://warehouse.meteor.com/bootstrap/")

(def install-dir "/opt/local/meteor")

(defn tarfile
  [version]
  (format "%s/meteor-bootstrap-$(uname)_$(uname -m).tar.gz" version))

(defn download-path [version]
  (format "%s%s" *bootstrap-url* (tarfile version)))

;;; # Settings
;;;
;;; We install from download
;;;
;;; Links:
;;; http://docs.meteor.com/

(defmulti-version-plan default-settings [version])

;; Download seems to be the only install method
(defmethod-version-plan
  default-settings {:os :os}
  [os os-version version]
  {:version (version-string version)
   :install-strategy ::download
   :install-dir install-dir
   :download {:url (download-path (version-string version))
              :unpack :tar}})


;;; ## Settings
(defn settings
  "Capture settings for meteor"
  [{:keys [version instance-id]
    :or {version (:version *meteor-defaults*)}
    :as settings}]
  (let [settings (deep-merge (default-settings version)
                             (dissoc settings :instance-id))]
    (debugf "meteor settings %s" settings)
    (assoc-settings facility settings {:instance-id instance-id})))

;;; # Install

(defmethod-plan crate-install/install ::download
  [facility instance-id]
  (let [{:keys [download install-dir]} (get-settings
                                        facility {:instance-id instance-id})]
    (apply-map remote-directory install-dir download)))

(defplan install
  [{:keys [instance-id]}]
  (crate-install/install facility instance-id))

;;; # Deploy bundle
(defplan rebuild-fibers
  "Rebuild fibers in a bundle."
  [bundle-path]
  (with-action-options
    {:script-dir (str (file bundle-path "programs" "server" "node_modules"))}
    (exec-checked-script
     "Rebuild fibers"
     (rm fibers :recursive true :force true)
     ("npm" "--color" false install "fibers@1.0.1"))))

(defplan npm-install
  "Install bundle dependencies."
  [bundle-path]
  (with-action-options
    {:script-dir (str (file bundle-path "programs" "server"))}
    (exec-checked-script
     "Install bundle dependencies"
     ("npm" "--color" false install))))

(defplan deploy-bundle
  "Deploy a bundle for meteor.

`bundle-file-spec` is a map of remote-directory arguments, specifying
  the bundle tgz."
  [target-path bundle-file-spec]
  (apply-map remote-directory target-path bundle-file-spec)
  (npm-install target-path))

(defn server-environment
  "Return a map of environment vars to use when running a meteor bundle."
  [{:keys [root-url port mongo-url mongo-oplog-url
           mail-url meteor-settings
           exec-with instance-id]
    :or {port 8080
         mongo-url "mongodb://localhost:27017/app"
         exec-with "node"}}]
  (-> {:PORT port
       :MONGO_URL mongo-url
       :ROOT_URL (or root-url (str "http://localhost:" port))}
      (maybe-assoc :MONGO_OPLOG_URL mongo-oplog-url)
      (maybe-assoc :MAIL_URL mail-url)
      (maybe-assoc :METEOR_SETTINGS meteor-settings)))

;;; # Server spec
(defn server-spec
  "Returns a service-spec for installing meteor."
  [{:keys [instance-id] :as settings}]
  (api/server-spec
   :phases {:settings (plan-fn
                        (pallet.crate.meteor/settings settings))
            :install (plan-fn
                       (install {:instance-id instance-id}))
            :deploy-bundle (fn [target-path bundle-file-spec]
                             (apply-map deploy-bundle target-path
                                        bundle-file-spec))}))
