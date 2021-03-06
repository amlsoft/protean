(ns protean.config
  (:require [environ.core :refer [env]]
            [me.rossputin.diskops :as d]))

;; =============================================================================
;; Configuration
;; =============================================================================

(defn os [] (System/getProperty "os.name"))

(defonce host (or (env :hostname) (.getCanonicalHostName (java.net.InetAddress/getLocalHost))))

(defn sim-port [] (or (env :protean-sim-port) "3000"))

(defn admin-port [] (or (env :protean-admin-port) "3001"))

(defn codex-dir [] (or (env :protean-codex-dir) (d/pwd)))

(defn asset-dir [] (or (env :protean-asset-dir) "public"))

(defn log-level [] (keyword (or (env :protean-log-level) "info")))

(defn res-dir [] (str (asset-dir) "/resource"))

(defn html-dir [] (str (asset-dir) "/html"))
