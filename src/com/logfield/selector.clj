(ns com.logfield.nio-wrapper.selector
  (:require [com.logfield.nio-wrapper.selection-key :as selection-key])
  (:import (java.nio.channels Selector ClosedSelectorException)))

(defn- selection-keys
  [selector]
  (try (into #{} (.keys selector))
       (catch ClosedSelectorException _ #{})))

(defn- selected-keys
  [selector]
  (try (into #{} (.selectedKeys selector))
       (catch ClosedSelectorException _ #{})))

(defn- selection-keys-info
  [selection-keys]
  (->> selection-keys
       (map selection-key/selection-key-info)
       (into #{})))

(defn selector-info
  [selector]
  {:selector selector
   :open? (.isOpen selector)
   :keys (selection-keys-info (selection-keys selector))
   :provider (.provider selector)
   :selected-keys (selection-keys-info (selected-keys selector))})

(defn close!
  [selector]
  (.close selector))

(defn selector!
  []
  (Selector/open))

(defn select!
  ([selector]
   (.select selector))
  ([selector timeout]
   (.select selector timeout)))

(defn select-now!
  [selector]
  (.selectNow selector))

(defn wakeup!
  [selector]
  (.wakeup selector))
