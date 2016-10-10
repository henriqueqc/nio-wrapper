(ns com.logfield.nio-wrapper.selection-key
  (:require [com.logfield.nio-wrapper.selectable-channel :as selectable-channel]
            [com.logfield.nio-wrapper.operation-set :as operation-set]))

(defn- interest-operations
  [selection-key]
  (operation-set/int->operations (.interestOps selection-key)))

(defn- ready-operations
  [selection-key]
  (operation-set/int->operations (.readyOps selection-key)))

(defn selection-key-info
  [selection-key]
  {:selection-key selection-key
   :attachment (.attachment selection-key)
   :channel (selectable-channel/selectable-channel-info (.channel selection-key))
   :interest-operations (interest-operations selection-key)
   :acceptable? (.isAcceptable selection-key)
   :connectable? (.isConnectable selection-key)
   :readable? (.isReadable selection-key)
   :valid? (.isValid selection-key)
   :writable? (.isWritable selection-key)
   :ready-operations (ready-operations selection-key)
   ;; Don't get the selector's info. Otherwise we will enter an infinite loop,
   ;; since the selector holds a reference to the selection-key.
   :selector (.selector selection-key)})

(defn attach!
  [selection-key object]
  (.attach selection-key object))

(defn cancel!
  [selection-key]
  (.cancel selection-key))

(defn set-interest-operations!
  [selection-key operations]
  (.interestOps selection-key (operation-set/operations->int operations)))
