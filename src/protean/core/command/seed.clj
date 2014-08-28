(ns protean.core.command.seed
  "Replace placeholder values a client provided set of values, grow seed
   values when incrementally negotiating (workflows etc)."
  (:require [clojure.string :as stg]
            [protean.core.transformation.coerce :as txco]
            [protean.core.codex.placeholder :as p]))

;; =============================================================================
;; Helper functions
;; =============================================================================

(defonce PSV-EXP "psv\\+")
(def azn "Authorization")

(defn- token [seed strat]
  (let [tokens (get-in seed [azn])]
    (first (filter #(.contains % strat) tokens))))

; TODO: needs refactoring, trying to get a prototype out
; strat is either Basic or Bearer
(defn- header-authzn-> [strat seed [method uri mp :as payload]]
  (if-let [auth (get-in mp [:headers azn])]
    (if (and (.contains auth p/psv) (.contains auth strat))
      (if-let [sauth (token seed strat)]
        (let [n (assoc-in mp [:headers azn]
                  (str strat " " (last (stg/split sauth #" "))))]
          (list method uri n))
        payload)
      payload)
    payload))

(defn- substr? [s sub] (if s (.contains s sub) false))

(defn- bag-item [v seed]
  (let [ns (first (.split v "/psv\\+"))]
    (first (filter #(substr? % (str ns "/")) (get-in seed ["bag"])))))

; first search in first class seed items, then in the bag
(defn- holder-swap [k v seed]
  (if (p/holder? v)
    (if-let [sv (get-in seed [(last (.split v PSV-EXP))])]
      sv
      (if-let [sv (bag-item v seed)] sv v))
    v))

(defn- swap-placeholders [k seed [method uri mp :as payload]]
  (if-let [phs (p/encode-value k (k mp))]
    (let [res (p/holders-swap phs holder-swap seed)]
      (list method uri (assoc mp k (p/encode-value k res))))
    payload))

; TODO: weak, only handles 1 instance of uri placeholder
(defn- uri-> [seed [method uri mp :as payload]]
  (if (p/uri-ns-holder? uri)
    (let [v (p/uri-ns-holder uri)
          sv (bag-item v seed)
          new-uri (stg/replace uri #"psv\+" (last (.split sv "/")))]
      (if sv (list method new-uri mp) payload))
    payload))

(defn- seed-> [test seed]
  (->> test
       (header-authzn-> "Basic" seed)
       (header-authzn-> "Bearer" seed)
       (swap-placeholders :query-params seed)
       (swap-placeholders :body seed)
       (swap-placeholders :form-params seed)
       (uri-> seed)))

(defn seeds [tests seed] (if seed (map #(seed-> % seed) tests) tests))
