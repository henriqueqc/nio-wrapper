(ns com.logfield.nio-wrapper.operation-set
  (:require [clojure.set :as set])
  (:import (java.nio.channels SelectionKey)))

(def operation->int {:accept  (SelectionKey/OP_ACCEPT)
                     :connect (SelectionKey/OP_CONNECT)
                     :read    (SelectionKey/OP_READ)
                     :write   (SelectionKey/OP_WRITE)})

(def int->operation (set/map-invert operation->int))

(def operations (-> operation->int
                    keys
                    set))

(defn int->operations
  [operations-int]
  (->> (map (partial bit-and operations-int) (keys int->operation))
       (filter (partial not= 0))
       (map int->operation)
       (set)))

(defn operations->int
  [operations]
  (->> operations
       (map operation->int)
       (reduce bit-or)))
