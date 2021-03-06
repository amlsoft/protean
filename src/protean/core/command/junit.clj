(ns protean.core.command.junit
  "Outputs to junit format."
  (:require [clojure.data.xml :as x]
            [clojure.string :as s]
            [clojure.java.io :refer [file]]
            [clojure.pprint :as pp]
            [protean.core.transformation.coerce :as co]
            [protean.config :as c])
  (:import java.io.File java.util.Calendar java.text.SimpleDateFormat))

(defn now-iso-8601 []
  (.format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ssZ")
    (.getTime (Calendar/getInstance))))

(defn- errors [results] (remove #(empty? (:error (:error %))) results))
(defn- failures [results] (remove #(empty? (:failures (:failures %))) results))

(defn- prop-to-xml [prop]
  (let [name (.getKey prop)
        value (.getValue prop)]
    (x/element :property {:name name :value value})))

(defn- error-to-xml [error]
  (let [type "exception class"
        stack ""]
    (x/element :error {:message error :type type} stack)))

(defn- failure-to-xml [failures]
  (let [type "exception class"
        stack ""
        to-xml (fn [msg] (x/element :failure {:message msg :type type} stack))]
    (map to-xml failures)))

(defn- testcase-to-xml [{:keys [entry request response error failures] :as result}]
  (let [status (:status response)
        name (str (:method entry) " " (:svc entry) " " (:path entry))
        classname "classname"
        time "time ms"
        out (str "Entry:" (co/pretty-js entry) "\n"
                 "Request:" (co/pretty-js request) "\n"
                 "Response:" (co/pretty-js response))]
    (x/element :testcase {:name name :classname classname :time time}
      (if error (error-to-xml error))
      (if (seq failures) (failure-to-xml failures))
      (x/element :system-out {} (x/cdata out)) ; TODO should bind *out* and send it to system-out?
      (x/element :system-err {} (x/cdata "")))))

(defn- write [[svc results]]
  (.mkdirs (file "target"))
  (let [file (str "target/TEST-" svc ".xml")
        tags (x/element :testsuite {:name svc
                                    :hostname c/host
                                    :tests (count results)
                                    :errors (count (errors results))
                                    :failures (count (failures results))
                                    :time "time s"
                                    :timestamp (now-iso-8601)}
               (x/element :properties {} (map prop-to-xml (System/getProperties)))
               (map testcase-to-xml results))]
    (with-open [out-file (java.io.FileWriter. file)]
      (x/emit tags out-file))
      (println "\n exported results in junit xml: " file)))

(defn write-report [results]
  (let [results-by-svc (group-by #(get-in % [:entry :svc]) results)]
    (doall (map write results-by-svc))))
