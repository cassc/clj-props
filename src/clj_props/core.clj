(ns clj-props.core
  (:require [clojure.edn :as edn]))

(def ^:private props-store (atom nil))

(defn set-props!
  "Set an edn file to load as properties"
  ([f]
   (set-props! f nil))
  ([f secure]
   (reset! props-store {:resource f :secure secure})))

(defn- load-props!
  "Load end file as clojure datastructure. If `secure` is true, use
  clojure.edn/read-string instead of clojure.core/read-string for
  reading edn file."
  []
  (swap! props-store assoc :props ((if (:secure @props-store)
                                     edn/read-string
                                     read-string)
                                   (slurp (:resource @props-store)))))

(defn- props-not-found-error
  [f k]
  (throw (RuntimeException. (str "Property " k " not found in " f))))

(defn props
  "options: 
  `:refresh` set to true to read property from file instead of cache
  `:default` is the default value of the property. Throws exception if the property is not set and no default value provided."
  ([key-or-keys]
   (props key-or-keys nil))
  ([key-or-keys {:keys [refresh default] :as options}]
   (when-not (:resource @props-store)
     (throw (RuntimeException. "Please call set-props! to configure property file first.")))
   (when (or refresh (not (:props @props-store)))
     (load-props!))
   (let [props (:props @props-store)
         val (get-in props (if (sequential? key-or-keys) key-or-keys [key-or-keys]) ::none)]
     (if (= val ::none)
       (or default (props-not-found-error (:resource @props-store) key-or-keys))
       val))))

