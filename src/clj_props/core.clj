(ns clj-props.core
  (:require [clojure.edn :as edn]))

(defn props-not-found-error
  [f k]
  (throw (RuntimeException. (str "Property " k " not found in " f)))
  nil)

(defn reload-if-required
  [store {:keys [refresh]}]
  (if (or (not (:props @store))
          refresh
          (not= (:last-mod @store) (.lastModified (:resource @store))))
    (swap! store assoc :props ((if (:secure @store)
                                 edn/read-string
                                 read-string)
                               (slurp (:resource @store))))))

(defmacro defconfig [name ^java.io.File f options]
  `(let [secure# ~(:secure options)
         props-store# (atom (assoc ~options
                                   :resource ~f
                                   :props ((if secure#
                                             edn/read-string
                                             read-string)
                                           (slurp ~f))
                                   :last-mod (.lastModified ~f)))]
     (defn ~name 
       ([key-or-keys#]
        (~name key-or-keys# nil))
       ([key-or-keys# options#]
        (reload-if-required props-store# options#)
        (let [props# (:props @props-store#)
              val# (get-in props# (if (sequential? key-or-keys#) key-or-keys# [key-or-keys#]) ::none)
              dval# (:default options# ::none)]
          (if (= val# ::none)
            (if (= dval# ::none)
              (props-not-found-error (:resource @props-store#) key-or-keys#)
              dval#)
            val#))))))
